# **Architecture**

Soundbox is designed with a 3-tier architecture. We have broken it into 4 components, the `presentation layer`, `business logic layer`, `data persistence layer`, and `domain specific objects`.

## Presentation layer

Our application has one main activity, `MainActivity`, which contains the main navigation bar at the bottom. It is complemented by fragments for each of the pages of the app. The `PlayingFragment`  allows users to play songs in their library. The `DiscoverFragment` implements the discover page and provides recommedations for songs for the user to listen to. `LibraryFragment` allow to show existing library by artist, album, genres, etc and allows searching of the user's lilbrary for songs by title, artist, etc. `PlaylistFragment` provides an interface for managing, creating, and playing custom playlists. All of these `Fragment`s are complemented by other, smaller fragments (Such as `PlaybackControlsFragment`), adapters (e.g., `RecListAdapter`), and other classes used to create the GUI. Small usage note: dragging up or tapping on the playback controls bar will give you more options, such as the ability to add songs.
In the Iteration 3, `SettingActivity`, `AnalyticsFragment`, and `DetailsFragment` classes are added and we changed `SearchFragment` to `LibraryFragment`. `SettingActivity` allows user to set their App theme, volume and visualization type. `AnalyticsFragment` implements the Analytics page to show user's listen time and the stat of what genre/artist/album they played, etc. `DetailsFragment` shows song details such as title name, artist, duration, album, genre, score and format.

## Business Logic layer

The logic layer of the application includes multiple classes including controllers, validators, loaders, and various useful utility classes. The controller classes are notified based on user behaviour and will update the model classes as necessary. The adapter classes act as a bridge between the UI and their respective data items (for example, Playlist objects for the PlaylistAdapter class), and validator classes are used by controllers to ensure that certain data are valid.
In the Iteration 3, `SongCollectionFilters` and `SongFilters` classes are added to filter/sort songs by date added, name, artist, album and genre.

## Data Persistence layer

This layer contains 3 interfaces `PlaylistPersistence`, `SongStatisticPersistence` and `SongPersistence`. Initially we created a stub database to represent fake song data. We now also have a `HSQLDB` implementation and a `Stub database` implementation as well. The database represented follows the singleton design pattern via the `Services` class that allows a user to reference the data persistance layer from anywhere inside the application.


## Domain Specific Objects

Domain specific objects represent data being passed throughout the application layers. We have represented our domain specific objects as `Song`, `Playlist`, `SongStatistic`, `Recommendation` and `SongCollection`.
`Song` represents a single song, `Playlist` represents a user created list of `Song` objects, `SongStatistic` represents and tracks details about a `Song` object such as number of plays, total listen time, and number of likes/dislikes, and `Recommendation` represents a recommended songs according to user's taste.
In the Iteration 3, `SongCollection` is added to show the sorted song collection for sorting option.

## Architectural Diagram

Note that this diagram contains only the major classes, and omits many of the helpers and more negligible objects.

![Fig. 1](https://code.cs.umanitoba.ca/comp3350-winter2020/my-cool-project-4/raw/master/Docs/Architecture.jpg)
