docker run \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -v /Users/tian/docker/mysql8.0/conf.d:/etc/mysql/conf.d \
  -v /Users/tian/docker/mysql8.0/data:/var/lib/mysql \
  -d \
  -p 3306:3306 \
  --name mysql8 \
  --platform linux/amd64 \
  amd64/mysql:8.0.29

docker run -d -p 6379:6379 --name redis --platform linux/amd64 amd64/redis:7.0.0

docker run -p 9000:9000 -p 9090:9090 \
  --net=bridge \
  --name minio \
  -d --restart=always \
  -e "MINIO_ACCESS_KEY=root" \
  -e "MINIO_SECRET_KEY=123456789" \
  -v /Users/tian/docker/minio/data:/data \
  -v /Users/tian/dodocker run -p 9000:9000 -p 9090:9090 \
  --net=bridge \
  --name minio \
  -d --restart=always \
  -e "MINIO_ACCESS_KEY=root" \
  -e "MINIO_SECRET_KEY=123456789" \
  -v /Users/tian/docker/minio/data:/data \
  -v /Users/tian/docker/minio/config:/config \
  minio/minio server \
  /data --console-address ":9090" -address ":9000"cker/minio/config:/config \
  minio/minio server \
  /data --console-address ":9090" -address ":9000"
