package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"time"

	"git.garena.com/caosz/hackathon2018/collect_data_server/protocol"
)

func collectDataHandler(w http.ResponseWriter, r *http.Request) {
	decoder := json.NewDecoder(r.Body)
	var rssiInfo protocol.RSSIInfo
	err := decoder.Decode(&rssiInfo)
	if err != nil {
		//w.WriteHeader(http.StatusInternalServerError)
		w.Write([]byte("err: " + err.Error()))
		return
	}
	resStr, _ := json.Marshal(rssiInfo)
	positionModel.Train(rssiInfo)
	w.Write([]byte("Finish"))
	fmt.Println(string(resStr))
	//dataFile.WriteString(string(resStr) + "\n")
	//dataFile.Sync()
	_, err = fmt.Fprintf(dataFile, "%s\n", string(resStr))
	if err != nil {
		w.Write([]byte("err: " + err.Error()))
		return
	}
	return
}

func predictHandler(w http.ResponseWriter, r *http.Request) {
	decoder := json.NewDecoder(r.Body)
	var predictRequest protocol.PredictRequest
	err := decoder.Decode(&predictRequest)
	if err != nil {
		w.Write([]byte("err: " + err.Error()))
		return
	}
	//resStr, _ := json.Marshal(predictRequest)
	ans := positionModel.Predict(predictRequest.Data)
	uid := predictRequest.UID
	if uid == "" {
		uid = "default"
	}
	ts := time.Now().Unix()
	jsonStr, _ := json.Marshal(predictRequest.Data)
	resp := &protocol.PredictResponse{Key: ans}
	respByte, _ := json.Marshal(resp)
	fmt.Println(predictRequest.UID, string(jsonStr), string(respByte))
	w.Write(respByte)
	dataManager.SavePos(&protocol.PosInfo{UID: uid, TimeStamp: ts, ID: string(ans)})
}

func getHeatMapHandler(w http.ResponseWriter, r *http.Request) {
	decoder := json.NewDecoder(r.Body)
	var getHeatMapRequest protocol.GetHeatMapRequest
	err := decoder.Decode(&getHeatMapRequest)
	if err != nil {
		w.Write([]byte("err: " + err.Error()))
		return
	}

	posInfos, err := dataManager.GetPosInfos(getHeatMapRequest.Start, getHeatMapRequest.End)
	if err != nil {
		w.Write([]byte("err: " + err.Error()))
		return
	}
	ansMap := make(map[string]int)
	for _, posInfo := range posInfos {
		ansMap[posInfo.ID] = ansMap[posInfo.ID] + 1
	}
	blks := []*protocol.Block{}
	for k, v := range ansMap {
		blks = append(blks, &protocol.Block{ID: k, Number: v})
	}
	resp := &protocol.GetHeatMapResponse{Blocks: blks}
	respByte, _ := json.Marshal(resp)
	w.Write(respByte)
}

func getPeopleHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("People")
	decoder := json.NewDecoder(r.Body)
	var getPeopleRequest protocol.GetPeopleRequest
	err := decoder.Decode(&getPeopleRequest)
	if err != nil {
		w.Write([]byte("err: " + err.Error()))
		return
	}

	people := []*protocol.Person{}
	for _, user := range getPeopleRequest.UIDs {
		posInfo, err := dataManager.GetUserPosInfo(user, 0, 1999999999)
		if err != nil {
			w.Write([]byte("err: " + err.Error()))
			return
		}
		if posInfo != nil {
			people = append(people, &protocol.Person{UID: user, Block: posInfo.ID, TimeStamp: posInfo.TimeStamp})
		}
	}
	resp := &protocol.GetPeopleResponse{People: people}
	respByte, _ := json.Marshal(resp)
	w.Write(respByte)
	fmt.Println(string(respByte))
}

func addDataHandler(w http.ResponseWriter, r *http.Request) {
	decoder := json.NewDecoder(r.Body)
	var addDataRequest protocol.AddDataRequest
	err := decoder.Decode(&addDataRequest)
	if err != nil {
		w.Write([]byte("err: " + err.Error()))
		return
	}
	dataManager.SavePos(&protocol.PosInfo{UID: addDataRequest.UID, ID: addDataRequest.Block, TimeStamp: addDataRequest.TimeStamp})
	w.Write([]byte("succeed"))
}
