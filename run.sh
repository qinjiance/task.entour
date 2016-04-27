#!/bin/bash
ulimit -SHn 51200
dir=`dirname $0`
pidfile=pid
cd  $dir
CP=.:config/:sslkey/
for file in lib/*;
do CP=${CP}:$file;
done
retval=0
# start the server
start(){
 	printf 'Starting bill task\n'
	if [ -f "$pidfile" ] ; then
		pid=`cat "$pidfile"`
    	printf 'Existing process: %d\n' "$pid"
  		retval=1
	else
		java  -Xms300M \
		      -Xmx300M \
		      -XX:+UseParallelGC \
		      -XX:+AggressiveOpts \
		      -XX:+UseFastAccessorMethods \
		      -XX:+HeapDumpOnOutOfMemoryError \
		      -verbose:gc \
		      -XX:+PrintGCDetails \
		      -XX:+PrintGCTimeStamps \
		      -cp $CP com.qinjiance.tourist.task.ChargeTask >/dev/null 2>&1  &
		echo $! >"$pidfile"
		if [ "$?" -eq 0 ] ; then
			printf 'Done\n'
		else
			printf 'the bill task could not start\n'
			retval=1
		fi
	fi
}
# stop the server
stop(){
  printf 'Stopping the bill task\n'
  if [ -f "$pidfile" ] ; then
    pid=`cat "$pidfile"`
    printf "Sending the terminal signal to the process: %s\n" "$pid"
    PROCESSPID=`ps -ef|awk  '{print $2}'|grep "$pid"`
    if [[ $PROCESSPID -ne "$pidfile" ]] ; then
    	rm -f "$pidfile";
        printf 'Done\n'
    fi
    kill -TERM "$pid"
    c=0
    while true ; do
      sleep 0.1
      PROCESSPID=`ps -ef|awk  '{print $2}'|grep "$pid"`
      if [[ $PROCESSPID -eq "$pidfile" ]] ; then
        c=`expr $c + 1`
        if [ "$c" -ge 100 ] ; then
          printf 'Hanging process: %d\n' "$pid"
          retval=1
          break
        fi
      else
        printf 'Done\n'
        rm -f "$pidfile";
        break
      fi
    done
  else
    printf 'No process found\n'
    retval=1
  fi
}
# dispatch the command
case "$1" in
start)
  start
  ;;
stop)
  stop
  ;;
restart)
  stop
  start
  ;;
hup)
  hup
  ;;
*)
  printf 'Usage: %s {start|stop|restart}\n'
  exit 1
  ;;
esac


# exit
exit "$retval"



# END OF FILE
