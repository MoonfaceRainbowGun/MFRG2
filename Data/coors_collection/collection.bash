block=10
echo "[" > $block

x=1
while [ $x -le 5 ]
do
  echo "[" >> $block
  /System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -s | grep Garena1 | sort | awk '{ print "{\"ssid\":\"", $1, "\", \"bssid\":\"", $2, "\", \"rssi\":", $3, "}," }' >> $block
  echo "]," >> $block
  x=$(( $x + 1 ))
done

echo "]" >> $block
