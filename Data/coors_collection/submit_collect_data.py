import json
import glob
import requests

files = glob.glob('train_data/*.json')

for file in files:
	with open(file) as f:
		info = json.load(f)
		data = []
		for l in info['data']:
			for c in l:
				c['ssid'] = c['ssid'].strip()
				c['bssid'] = c['bssid'].strip()
				data.append(c)
		info['data'] = data

		requests.post('http://203.116.214.78:2546/train/', data=json.dumps(info))
		f.close()
		
