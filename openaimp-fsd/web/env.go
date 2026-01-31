package main

import (
	"context"
	"github.com/sethvargo/go-envconfig"
)

type ServerConfig struct {
	ListenAddr string `env:"LISTEN_ADDR, default=:8000"` // HTTP listen address

	FsdHttpServiceAddress string `env:"FSD_HTTP_SERVICE_ADDRESS, required"` // HTTP address to talk to the FSD http service
	FsdServerHostname     string `env:"FSD_SERVER_HOSTNAME, default=localhost"`
	FsdServerIdent        string `env:"FSD_SERVER_IDENT, default=OPENFSD"`
	FsdServerLocation     string `env:"FSD_SERVER_LOCATION, default=Earth"`
	ApiServerBaseURL      string `env:"API_SERVER_BASE_URL, default=http://localhost:8000"`
}

func loadServerConfig(ctx context.Context) (config *ServerConfig, err error) {
	config = &ServerConfig{}
	if err = envconfig.Process(ctx, config); err != nil {
		return
	}
	return
}
