cd ./../../backend && docker build . --pull -t ioque/backend
cd ./../frontend && docker build . --build-arg REACT_APP_API_URL="http://192.168.0.62:8080" --pull -t ioque/frontend