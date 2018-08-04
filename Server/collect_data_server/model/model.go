package model

import (
	"bufio"
	"encoding/json"
	"fmt"
	"log"
	"math"
	"os"

	"git.garena.com/caosz/hackathon2018/collect_data_server/protocol"
)

//DataSet int[0] -> meanVal int[1] -> num
type DataSet map[string][]int

type Model struct {
	dataSetMap map[string]DataSet
}

func readLine(fileName string) []protocol.RSSIInfo {
	f, err := os.Open(fileName)
	if err != nil {
		log.Fatal(err)
	}
	ansList := []protocol.RSSIInfo{}
	buf := bufio.NewReader(f)
	for {
		line, err := buf.ReadString('\n')
		//fmt.Println(line)
		if err != nil {
			return ansList
		}
		var rssiInfo protocol.RSSIInfo
		err = json.Unmarshal([]byte(line), &rssiInfo)
		if err == nil {
			ansList = append(ansList, rssiInfo)
		} else {
			return ansList
		}
	}
}

//NewModel Return a model which have a model and can
func NewModel(fileName string) *Model {
	allInfos := readLine(fileName)
	model := &Model{}
	model.dataSetMap = make(map[string]DataSet)
	fmt.Println("NewModel")
	for _, info := range allInfos {
		model.Train(info)
	}
	return model
}

//Train Train the model with given rssi infos
func (m *Model) Train(info protocol.RSSIInfo) {
	fmt.Println("Train")
	dataSetMap := m.dataSetMap
	key := info.ID
	dataSet, have := dataSetMap[key]
	if !have {
		dataSet = make(map[string][]int)
	}
	for _, data := range info.Data {
		curVal, have := dataSet[data.BSSID]
		if !have {
			curVal = make([]int, 2)
		}
		curVal[0] = (curVal[0]*curVal[1] + data.RSSI) / (curVal[1] + 1)
		curVal[1]++
		dataSet[data.BSSID] = curVal
	}
	dataSetMap[key] = dataSet

	m.dataSetMap = dataSetMap
	return
}

//Predict Given a set of rssi infos, return the most similar reference point
func (m *Model) Predict(infos []protocol.SingleRSSI) string {
	fmt.Println("Predict")
	dataSetMap := m.dataSetMap
	bestVal := 0.0
	selected := ""
	for k, v := range dataSetMap {
		if k == "" || k == "I" {
			continue
		}
		similar := getSimilarities(infos, v)
		fmt.Println("similar", similar, "k", k)
		if similar > bestVal {
			bestVal = similar
			selected = k
			// if k == "Y1" || k == "Y2" || k == "Y3" {
			// 	selected = "Y"
			// }
			if len(k) >= 2 {
				if k[0] != 'I' {
					if k[0] >= 'a' && k[0] <= 'z' {
						selected = string(k[0] - 'a' + 'A')
					} else {
						selected = string(k[0])
					}
				}
			}
		}
	}
	return selected
}

func getSimilarities(a []protocol.SingleRSSI, b DataSet) float64 {
	sims := float64(0)
	used := make(map[string]bool)
	cnt := 0
	for i := 0; i < len(a); i++ {
		//Fix : -50 -> 50, -100 -> 0
		val1 := fixValue(float64(a[i].RSSI))
		val2Int, have := b[a[i].BSSID]
		val2 := float64(-100)
		if have {
			val2 = float64(val2Int[0])
		}
		val2 = fixValue(val2)
		used[a[i].BSSID] = true
		cnt++
		sims += (val1 + val2) / 2 * (15 - math.Abs(val1-val2))
	}
	// for k, v := range b {
	// 	if used[k] == false {
	// 		used[k] = true
	// 		val1 := 0.0
	// 		val2 := float64(v[0] + 100)
	// 		cnt++
	// 		sims += (val1 + val2) / 2 * (15 - math.Abs(val1-val2))
	// 	}
	// }
	return sims / float64(cnt)
}

func fixValue(a float64) float64 {
	return a + 100
}

func max(a, b float64) float64 {
	if a > b {
		return a
	}
	return b
}

func getKey(x, y int) string {
	return fmt.Sprintf("%d.%d", x, y)
}
