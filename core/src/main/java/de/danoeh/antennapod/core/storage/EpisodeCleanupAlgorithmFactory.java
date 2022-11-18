package de.danoeh.antennapod.core.storage;


public abstract class EpisodeCleanupAlgorithmFactory {
    public static EpisodeCleanupAlgorithm build() {

                return new APCleanupAlgorithm(0);
    }
}
