# sudo chmod +x up_database.sh
# ./up_database.sh

docker build -t vocatio .
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5434/vocatio_db" \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=password \
  -e JWT_SECRET="cambia-esto" \
  vocatio


# Trouble shooting

# host.docker.internal remplaza por la ip del host (si no tienes docker desktop)