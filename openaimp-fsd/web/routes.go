package main

import (
	"github.com/gin-gonic/gin"
	"os"
)

func (s *Server) setupRoutes() (e *gin.Engine) {
	e = gin.New()
	e.Use(gin.Recovery())
	if os.Getenv("GIN_LOGGER") != "" {
		e.Use(gin.Logger())
	}

	e.GET("/whazzup.json", s.getDatafeed)

	return
}
