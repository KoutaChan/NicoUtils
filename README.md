# NicoUtils

## はじめに
 - APIではない物も使用しています
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

### TODO:
- [ ] ライブ取得を完全に取得できるようにする (~ 93%) (.tsのみデバッグ完了)
- [ ] 検索機能に対応する (~ 0%)
- [ ] ニコニコ大百科を取得できるようにする (~ 0%)


- [ ] ログイン機能に対応させる (~ 40%)