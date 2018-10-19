# KPP - KSP Part Purger

I like to share a small tool I have written in Java / JavaFX to "manage" part configuration files of my KSP installation.

There are certain parts in the game and some of the mods which I personally tend to ignore because of several reasons. Instead of using other mods like "Janitors Closet" to remove those parts from the game, I wanted a small tool which just renames some part configuration files so the game does not load them at all. The tool has to make it easy to find the parts by name or title and shall give an overview of what is active and purged in an installation folder. Further more, after mod updates via ckan, the freshly updated but previously purged parts shall stay purged but still updated. Also, a list of purged parts of a certain installation has to be persistable in JSON files so the tool can reload and purge parts from another installation directory.

So, I wrote the "KSP Part Purger" to fulfill my needs. If someone wants to use it, feel free to do so. Please be aware, that vessels in existing savegames containing parts which you have purged, will not be loaded.

Use the tool with caution.

Download the latest release: https://github.com/craidler/KPP/releases

Inspect source: https://github.com/craidler/KPP

Screenshot:

![Screenshot of KPP](https://github.com/craidler/KPP/edit/master/KPP.01.png)
