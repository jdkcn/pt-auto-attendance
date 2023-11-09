# The pt auto attendance

copy config file and run

```bash
cp src/main/resources/application.sample.yml src/main/resources/application.yml
```

## Use docker

```bash
docker run -itd -m 100m \
  --name pt-auto-attendance \
  --hostname pt \
  --env TZ="Asia/Shanghai" \
  -p 8900:8900 \
  -v pt-auto-attendance-volume/config:/home/spring/config \
  jdkcn/pt-auto-attendance:latest
```

copy the config file `src/main/resources/application.sample.yml` to `pt-auto-attendance-volume/config/application.yml` and change the file content
