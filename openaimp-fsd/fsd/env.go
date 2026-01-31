package fsd

import (
	"context"

	"github.com/sethvargo/go-envconfig"
)

type ServerConfig struct {
	FsdListenAddrs []string `env:"FSD_LISTEN_ADDRS, default=:6809"` // FSD listen addresses

	NumMetarWorkers int `env:"NUM_METAR_WORKERS, default=4"` // Number of METAR fetch workers to run

	ServiceHTTPListenAddr string `env:"SERVICE_HTTP_LISTEN_ADDR, default=:13618"`
	AuthAPIEndpoint       string `env:"AUTH_API_ENDPOINT, default=http://127.0.0.1:6067/fsd/login"`
	WelcomeMessage        string `env:"WELCOME_MESSAGE, default=Connected to openfsd"`
}

func loadServerConfig(ctx context.Context) (config *ServerConfig, err error) {
	config = &ServerConfig{}
	if err = envconfig.Process(ctx, config); err != nil {
		return
	}
	return
}
