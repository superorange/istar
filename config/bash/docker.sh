#docker run \
#  -e MYSQL_ROOT_PASSWORD=123456 \
#  -v /Users/tian/docker/mysql8.0/conf.d:/etc/mysql/conf.d \
#  -v /Users/tian/docker/mysql8.0/data:/var/lib/mysql \
#  -d \
#  -p 3310:3306 \
#  --name mysql8 \
#  --platform linux/amd64 \
#  amd64/mysql:8.0

docker run -d \
  -e POSTGRES_PASSWORD=123456 \
  -e PGDATA=/var/lib/postgresql/data/pgdata \
  -v /Users/tian/docker/postgresql14.3/data:/var/lib/postgresql/data \
  -p 5410:5432 \
  --platform linux/amd64 \
  --name postgres14.3 \
  amd64/postgres:14.3
