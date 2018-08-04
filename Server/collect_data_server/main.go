package main

import (
	"net/http"
	"os"

	"git.garena.com/caosz/hackathon2018/collect_data_server/data"
	"git.garena.com/caosz/hackathon2018/collect_data_server/model"
)

const dataFileName = "data.out"

var (
	positionModel *model.Model
	dataFile      *os.File
	dataManager   *data.Client
)

func main() {
	var err error
	a, err := os.Open(dataFileName)
	if err != nil {
		b, err := os.Create(dataFileName)
		if err != nil {
			panic(err)
		}
		b.Close()
	}
	a.Close()
	dataFile, err = os.OpenFile(dataFileName, os.O_APPEND|os.O_WRONLY, 0644)
	if err != nil {
		panic(err)
	}
	positionModel = model.NewModel(dataFileName)
	dataManager = data.NewClient()
	http.HandleFunc("/train/", collectDataHandler)
	http.HandleFunc("/predict/", predictHandler)
	http.HandleFunc("/getheatmap/", getHeatMapHandler)
	http.HandleFunc("/getpeople/", getPeopleHandler)
	http.HandleFunc("/adddata/", addDataHandler)

	http.ListenAndServe(":2546", nil)
}
