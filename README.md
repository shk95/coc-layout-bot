# coc-layout-bot

클래시 오브 클랜 배치도를 유튜브에서 찾아 디스코드에 올려 주는 봇입니다.

배치도를 올리는 유튜브 채널을 등록해 두면, 새 영상이 올라왔을 때 화면을 잘라 이미지로 만들고 디스코드 채널에 올립니다. 영상을 처음부터 끝까지 넘겨 보며 쓸 만한 배치를 찾는 게 번거로워서 만들었습니다.

## 어떻게 도나요

```
등록된 유튜브 채널 → 새 영상 확인 → 구간을 나눠 캡처 요청 → 이미지 링크 → 디스코드 전송
```

캡처는 따로 떼어 둔 [yt-media-extractor-api](https://github.com/shk95/yt-media-extractor-api)가 합니다. 이 봇은 언제 무엇을 캡처할지만 정합니다.

## 무엇으로 만들었나요

- Java 17 · Spring Boot 3.2 · JDA · Oracle DB
- Docker로 감싸 원격 서버에서 돌렸습니다. 봇과 캡처 API를 compose로 한 네트워크에 묶었습니다
- `master`에 푸시하면 GitHub Actions가 이미지를 만들어 Docker Hub에 올립니다 (amd64 / arm64)

## 실행

`.env`의 빈 값을 채우고 실행합니다.

```bash
docker compose up -d
```
