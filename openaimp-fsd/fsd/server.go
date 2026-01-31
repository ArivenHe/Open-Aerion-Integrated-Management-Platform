package fsd

import (
	"context"
	"errors"
	"fmt"
	"log/slog"
	"net"
	"sync"
)

type Server struct {
	cfg          *ServerConfig
	postOffice   *postOffice
	metarService *metarService
}

// NewServer creates a new Server instance.
//
// See NewDefaultServer to create a server using default settings obtained via environment variables.
func NewServer(cfg *ServerConfig, numMetarWorkers int) (server *Server, err error) {
	server = &Server{
		cfg:          cfg,
		postOffice:   newPostOffice(),
		metarService: newMetarService(numMetarWorkers),
	}
	return
}

// NewDefaultServer creates a new Server instance using the default configuration obtained via environment variables
func NewDefaultServer(ctx context.Context) (server *Server, err error) {
	config, err := loadServerConfig(ctx)
	if err != nil {
		return
	}

	slog.Info(fmt.Sprintf("auth endpoint %s", config.AuthAPIEndpoint))

	if server, err = NewServer(config, config.NumMetarWorkers); err != nil {
		return
	}

	return
}

func (s *Server) Run(ctx context.Context) (err error) {
	// Start metar service
	go s.metarService.run(ctx)

	// Start HTTP service
	go s.runServiceHTTP(ctx)

	errCh := make(chan error, len(s.cfg.FsdListenAddrs))
	var listenerWg sync.WaitGroup

	for _, addr := range s.cfg.FsdListenAddrs {
		slog.Info(fmt.Sprintf("Listening on %s\n", addr))
		listenerWg.Add(1)
		go func(ctx context.Context, addr string) {
			defer listenerWg.Done()
			s.listen(ctx, addr, errCh)
		}(ctx, addr)
	}

	// Collect startup errors
	go func() {
		listenerWg.Wait()
		close(errCh)
	}()

	var startupErrors []error
	for err := range errCh {
		startupErrors = append(startupErrors, err)
	}

	if len(startupErrors) > 0 {
		return fmt.Errorf("some listeners failed: %v", startupErrors)
	}

	// All listeners started successfully; wait for context to be cancelled
	<-ctx.Done()

	return
}

func (s *Server) listen(ctx context.Context, addr string, errCh chan<- error) {
	config := net.ListenConfig{}
	listener, err := config.Listen(ctx, "tcp4", addr)
	if err != nil {
		errCh <- fmt.Errorf("failed to listen on %s: %w", addr, err)
		return
	}
	defer listener.Close()

	// Start a goroutine to close the listener when the context is cancelled
	go func() {
		<-ctx.Done()
		listener.Close()
	}()

	// Accept connections in a loop
	for {
		conn, err := listener.Accept()
		if err != nil {
			if errors.Is(err, net.ErrClosed) {
				// Listener was closed due to context cancellation; exit the loop
				return
			}
			// Log or handle non-fatal accept errors
			continue
		}
		// Handle the connection in another goroutine
		go s.handleConn(ctx, conn)
	}
}
