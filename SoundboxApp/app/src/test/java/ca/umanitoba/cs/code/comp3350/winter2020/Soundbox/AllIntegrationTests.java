package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaylistControllerIT;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongControllerIT;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongStatisticControllerIT;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SongControllerIT.class,
        PlaylistControllerIT.class,
        SongStatisticControllerIT.class
})

public class AllIntegrationTests {

}
