package me.koutachan.nicoutils.test;

import me.koutachan.nicoutils.NicoUtils;
import me.koutachan.nicoutils.impl.options.enums.video.VideoType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class NicoVideoTest {

    @Benchmark
    public void run() {
        NicoUtils.getVideoBuilder()
                .setURL("https://www.nicovideo.jp/watch/sm9")
                .setVideoType(VideoType.HTTP)
                .create();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(NicoVideoTest.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}