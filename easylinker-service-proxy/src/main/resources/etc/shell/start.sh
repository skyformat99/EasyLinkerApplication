
which "nohup" > /dev/null
if [ $? -eq 0 ]
then
echo "Starting ......"
nohup java -jar EasyLinkerApplication-0.0.1-SNAPSHOT.jar --spring.config.location = application.properties &
echo "Finished!"
else
echo "Please install nobup with this command:apt-get install nohup"
fi
