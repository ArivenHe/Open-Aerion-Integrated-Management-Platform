package main

import (
	"context"
	"log/slog"
	"net"
)

type Server struct {
	cfg *ServerConfig
}

func NewDefaultServer(ctx context.Context) (server *Server, err error) {
	cfg, err := loadServerConfig(ctx)
	if err != nil {
		return
	}

	if server, err = NewServer(cfg); err != nil {
		return
	}

	return
}

func NewServer(cfg *ServerConfig) (server *Server, err error) {
	server = &Server{
		cfg: cfg,
	}

	return
}

func (s *Server) Run(ctx context.Context) (err error) {
	e := s.setupRoutes()
	go s.runDatafeedWorker(ctx)

	listener, err := net.Listen("tcp", s.cfg.ListenAddr)
	if err != nil {
		return
	}
	defer listener.Close()

	go func() {
		if err := e.RunListener(listener); err != nil {
			slog.Error(err.Error())
		}
	}()

	<-ctx.Done()

	return
}
