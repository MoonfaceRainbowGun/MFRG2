package protocol

//SingleRSSI Information for a single BSSID
type SingleRSSI struct {
	BSSID string `json:"bssid"`
	RSSI  int    `json:"rssi"`
	SSID  string `json:"ssid"`
}

//RSSIInfo The basic entry of record
type RSSIInfo struct {
	ID   string       `json:"id"`
	Data []SingleRSSI `json:"data"`
}

//PredictRequest The basic entry of record
type PredictRequest struct {
	UID  string       `json:"uid"`
	Data []SingleRSSI `json:"data"`
}

//PredictResponse The basic entry of record
type PredictResponse struct {
	Key string `json:"key"`
}

//PosInfo The basic entry of record
type PosInfo struct {
	UID       string `json:"uid"`
	TimeStamp int64  `json:"ts"`
	ID        string `json:"id"`
}

//GetHeatMapRequest The basic entry of record
type GetHeatMapRequest struct {
	Start int64 `json:"start"`
	End   int64 `json:"end"`
}

//Block The block area which contains a ID and number
type Block struct {
	ID     string `json:"id"`
	Number int    `json:"number"`
}

//GetHeatMapResponse The basic entry of record
type GetHeatMapResponse struct {
	Blocks []*Block `json:"blks"`
}

//GetPeopleRequest The basic entry of record
type GetPeopleRequest struct {
	Start int64    `json:"start"`
	End   int64    `json:"end"`
	UIDs  []string `json:"uids"`
}

//Person The position status of a single user
type Person struct {
	UID       string `json:"uid"`
	Block     string `json:"id"`
	TimeStamp int64  `json:"timestamp"`
}

//GetPeopleResponse The basic entry of record
type GetPeopleResponse struct {
	People []*Person `json:"people"`
}

//AddDataRequest Inject data directly
type AddDataRequest struct {
	UID       string `json:"uid"`
	Block     string `json:"id"`
	TimeStamp int64  `json:"timestamp"`
}
