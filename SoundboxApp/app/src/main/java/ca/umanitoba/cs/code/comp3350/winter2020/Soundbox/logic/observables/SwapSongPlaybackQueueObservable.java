package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables;

import java.util.Arrays;

public class SwapSongPlaybackQueueObservable extends SoundboxObservable<int[]> {
    private int[] positions;

    @Override
    public void setValue(int[] positions) {
        this.positions = Arrays.copyOf(positions, positions.length);
        setChanged();
        notifyObservers();
    }

    /**
     * @return Returns the song that was deleted. If more than one song was added returns null
     */
    @Override
    public int[] getValue() {
        return positions;
    }

}
