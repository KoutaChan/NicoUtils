# NicoUtils

## はじめに
 - 自己責任で使用してください

## ライセンス
 - [MIT](LICENSE)
 
## APIを使う

### 動画リンクを取得
```
import me.koutachan.nicoutils.NicoUtils;
import me.koutachan.nicoutils.impl.options.enums.video.VideoType;

public class test {
    public static void main(String[] args) {
        String url = NicoUtils.getVideoBuilder()
                .setVideoType(VideoType.HTTP)
                .setURL("https://www.nicovideo.jp/watch/sm9")
                .create()
                .getVideoURL();

        // 出力 (2~3分程度しか維持されません) リンクを維持したい場合は NicoUtils.getVideoBuilder().setHeartBeat(true) を使用してください
        // https://vodedge059.dmc.nico/vod/ht2_nicovideo/nicovideo-sm9_25c45ed67840226ae2d2ffd6f7835fb3a630cb08a91869aeaebbef38b9f7c73a?ht2_nicovideo=6-NmQhTIOpD0_1658028873624.kbmg3mrubs_rf5b9m_3es0eo58iapz9
        System.out.println(url);
    }
}
```

### 動画をダウンロード
```
import me.koutachan.nicoutils.NicoUtils;
import me.koutachan.nicoutils.impl.options.enums.video.VideoType;

import java.io.File;
import java.nio.file.Paths;

public class test {
    public static void main(String[] args) {
        NicoVideoInfo info = NicoUtils.getVideoBuilder()
                .setVideoType(VideoType.HTTP)
                .setHeartBeat(true)
                .setURL("https://www.nicovideo.jp/watch/sm9")
                .create();

        File file = Paths.get("", "test.mp4").toFile();

        long start = System.currentTimeMillis();

        info.asyncDownload(file, v -> {
            info.stopHeartBeat();

            System.out.printf("output=%s time(ms)=%s%n", file.getAbsolutePath(), System.currentTimeMillis() - start);
        });
    }
}
```
### 動画をダウンロード (シンプル)
```
import me.koutachan.nicoutils.NicoUtils;
import me.koutachan.nicoutils.impl.options.enums.video.VideoType;

import java.nio.file.Paths;

public class test {
    public static void main(String[] args) {
        NicoUtils.getVideoBuilder()
                .setVideoType(VideoType.HTTP)
                .setHeartBeat(true)
                .setURL("https://www.nicovideo.jp/watch/sm9")
                .create()
                .asyncDownload(Paths.get("", "test.mp4").toFile(), NicoVideoInfo::stopHeartBeat);
    }
}
```

### BentchMark
| Benchmark                | Mode  | Cnt | Score  | Error  | Units  |
|--------------------------|-------|-----|--------|--------|--------|
| NicoDownloadFileTest.run | thrpt | 5   | ≈ 10⁻⁵ |        | ops/ms |
| NicoVideoTest.run        | thrpt | 5   | 0.003  | ±0.001 | ops/ms |

### TODO:
- [ ] ライブ取得を完全に取得できるようにする (~ 85%)
- [ ] 検索機能を取得できるようにする (~ 0%)
- [ ] ニコニコ大百科を取得できるようにする (~ 0%)


- [ ] ログイン機能に対応させる (~ ??)