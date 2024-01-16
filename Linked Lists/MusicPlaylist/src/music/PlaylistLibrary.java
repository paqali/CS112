package music;
import java.io.File;
import java.util.*;

import org.w3c.dom.Node;

public class PlaylistLibrary {

    private ArrayList<Playlist> songLibrary; // contains various playlists

    public PlaylistLibrary(ArrayList<Playlist> songLibrary) {
        this.songLibrary = songLibrary;
    }

   
    public PlaylistLibrary() {
        this(null);
    }

    public Playlist createPlaylist(String filename) {
        Playlist pl = new Playlist(); 
        StdIn.setFile(filename);
        if (StdIn.isEmpty()){pl.setSize(0); pl.setLast(null); return pl;}

         SongNode last = null;

        while (!StdIn.isEmpty()) {
            String[] data = StdIn.readLine().split(",");
            String n = data[0];
            String a = data[1];
            int y = Integer.parseInt(data[2]);
            int p = Integer.parseInt(data[3]);
            String l = data[4];

            Song song = new Song(n, a, y, p, l);
            SongNode newN = new SongNode(song,null);

            if (pl.getLast() == null) {
                pl.setLast(newN);
                newN.setNext(pl.getLast()); // becomes looped (circular) points to itself
            } else {
                newN.setNext(pl.getLast().getNext()); 
                pl.getLast().setNext(newN);
                pl.setLast(newN);
            }

            pl.setSize(pl.getSize() + 1);
        }
        
        return pl;
    }

  
    public void addPlaylist(String filename, int playlistIndex) {
        
        /* DO NOT UPDATE THIS METHOD */

        if ( songLibrary == null ) {
            songLibrary = new ArrayList<Playlist>();
        }
        if ( playlistIndex >= songLibrary.size() ) {
            songLibrary.add(createPlaylist(filename));
        } else {
            songLibrary.add(playlistIndex, createPlaylist(filename));
        }        
    }

    public boolean removePlaylist(int playlistIndex) {
        /* DO NOT UPDATE THIS METHOD */

        if ( songLibrary == null || playlistIndex >= songLibrary.size() ) {
            return false;
        }

        songLibrary.remove(playlistIndex);
            
        return true;
    }
    
    public void addAllPlaylists(String[] filenames) {
        
        // WRITE YOUR CODE HERE
        songLibrary = new ArrayList<Playlist>();
        for(int i = 0; i < filenames.length; i++){addPlaylist(filenames[i], i);}
    }

    public boolean insertSong(int playlistIndex, int position, Song song) {
        
        if (playlistIndex < 0 || playlistIndex >= songLibrary.size()) {return false;}
        int maxsize = songLibrary.get(playlistIndex).getSize() + 1;
        if(position < 1 || position > maxsize) {return false;}

        Playlist pl = songLibrary.get(playlistIndex);


        SongNode newSong = new SongNode(song, null);

        if (pl.getSize() == 0) {
            newSong.setNext(newSong); //if only song in playlist then it loops to itself
            pl.setLast(newSong);
            return true;
        }

        if (position == 1) {
            // Inserting at the beginning
            newSong.setNext(pl.getLast().getNext());
            pl.getLast().setNext(newSong);
            return true;
        }

        // Inserting at a position other than the beginning
        SongNode curr = pl.getLast().getNext();
        int i = 1;
        while(i < position -1){
            curr = curr.getNext();
            i++;
        }
        newSong.setNext(curr.getNext());
        curr.setNext(newSong);

        if (position == maxsize) {
            pl.setLast(newSong);
        }
        pl.setSize(maxsize);
        return true;
    }


    public boolean removeSong(int playlistIndex, Song song){
        Playlist pl = songLibrary.get(playlistIndex);
        if (pl.getSize() == 0)
            return false;
        SongNode first = pl.getLast().getNext(); 
        SongNode last = pl.getLast();

        while (first != pl.getLast()) {
            if (song.equals(first.getSong())) {
                last.setNext(first.getNext()); // essentially last.next = first.next skipping over first

                if (first == pl.getLast()){
                    pl.setLast(last);
                }
                pl.setSize(pl.getSize() - 1);
                return true;
            }

            last = first;
            first = first.getNext();
        }

        return false;
    }


    public void reversePlaylist(int playlistIndex) {
        // WRITE YOUR CODE HERE

        Playlist pl = songLibrary.get(playlistIndex); // 1 -> 2 -> 3 -> 4        4 -> 3 -> 2 -> 1
        if(pl.getSize() == 0)
            return;

        SongNode head = pl.getLast().getNext(); // current
        SongNode pre = pl.getLast();
        SongNode curr = head; //first

        do{
            SongNode placeholder = curr.getNext();
            curr.setNext(pre);
            pre = curr;
            curr = placeholder;

        } while(head != curr);

        pl.setLast(curr);
    }

    public void mergePlaylists(int playlistIndex1, int playlistIndex2) {
      
        int low = Math.min(playlistIndex1, playlistIndex2);  //finding lower playlist and higher playlist
        int high = Math.max(playlistIndex1, playlistIndex2);
        
        Playlist lpl = songLibrary.get(low);
        Playlist hpl = songLibrary.get(high);
        int s1 = lpl.getSize();
        int s2 = hpl.getSize();
        SongNode last = null;
        SongNode first = null;
        
        if(lpl.getSize() == 0){songLibrary.set(low, hpl); return;} // starter cases if either playlist empty

        if(hpl.getSize() == 0){ return;} 

        while (lpl.getSize() > 0 && hpl.getSize() > 0) {
            SongNode lcurr = lpl.getLast().getNext();
            SongNode hcurr = hpl.getLast().getNext(); // starting at firsts for each low and high playlists

            SongNode temp;

            if (lcurr.getSong().getPopularity() >= hcurr.getSong().getPopularity()) {
                temp = lcurr;
                lpl.getLast().setNext(lcurr.getNext());
                if (lcurr == lpl.getLast()) {
                    lpl.setLast(null);
                }
                lpl.setSize(lpl.getSize() - 1); // reduce size each time
            } else {
                temp = hcurr;
                hpl.getLast().setNext(hcurr.getNext());
                if (hcurr == hpl.getLast()) {
                    hpl.setLast(null);
                }
                hpl.setSize(hpl.getSize() - 1); // reduce size each time
            }

            if (last == null) {
                last = temp;
                first = temp;
                temp.setNext(temp);
            } else {
                temp.setNext(first);
                last.setNext(temp);
                last = temp;
            }
        }
        Playlist notEmpty; // to deal with left over values after original traversal. 
        if(lpl.getSize() > 0){
            notEmpty = lpl;
        }
        else 
            notEmpty = hpl;

        if (last == null) {
            last = notEmpty.getLast();
            first = notEmpty.getLast().getNext();
        } else if (!(notEmpty.getLast() == null)) {
            last.setNext(notEmpty.getLast().getNext());
            notEmpty.getLast().setNext(first);
        }

        lpl.setLast(last);
        lpl.setSize(lpl.getSize() + hpl.getSize());
        removePlaylist(high);

        if (!(last == null)) {
            SongNode curr = last.getNext(); 
            SongNode highest = last;
            int pop = last.getSong().getPopularity();

            do {
                if (curr.getSong().getPopularity() > pop) { // do while loop so code runs once before condition is checked
                    pop = curr.getSong().getPopularity();
                    highest = curr;
                }
                curr = curr.getNext();
            } while (curr != last.getNext());

            SongNode higher = last;
            while (higher.getNext() != highest) {
                higher = higher.getNext();
            }
            lpl.setLast(higher);
            lpl.setSize(s1 + s2); //after playlists are merged the size of the lowerplaylist has to be lpl size + hpl size
        }
    }

    public void shufflePlaylist(int playlistIndex){
        Playlist pls = songLibrary.get(playlistIndex);
        Playlist shuffled = new Playlist();
        StdRandom.setSeed(2023);
        int size = pls.getSize();
        if (pls == null || pls.getSize() <= 1) {return;} // checks if playlist is null or at 1, because you cant shuffle a playlist w 1 song.

        while (pls.getSize() > 0) {
            int rand = StdRandom.uniformInt(pls.getSize() + 1);
            SongNode curr = pls.getLast().getNext();
            SongNode pre = pls.getLast();
            SongNode temp;

            if (rand == 1) {
                temp = curr;
                pls.getLast().setNext(curr.getNext()); // will essentially remove the "first" song
                if (curr == pls.getLast()) {
                    pls.setLast(pre);
                }
            } else {
                int i = 1; // i is starting from 1 because we are its based off position and index, and also we dealt with the case if rand == 1
                while (i < rand) {// traverse until you hit random index
                    pre = curr;
                    curr = curr.getNext();
                    i++;
                }

                temp = curr;
                pre.setNext(curr.getNext());

                if (curr == pls.getLast()) { pls.setLast(pre); } // if the random index is the last song we remove the last song by setting the last node to the song before it
            }

            if (shuffled.getLast() == null) {
                shuffled.setLast(temp); // if shuffled playlist is empty it starts the circular loop by pointing it to itself
                temp.setNext(temp);
            } else {
                temp.setNext(shuffled.getLast().getNext());
                shuffled.getLast().setNext(temp);
            }

            pls.setSize(pls.getSize() - 1);
        }
        shuffled.setSize(size);
        songLibrary.set(playlistIndex, shuffled);
    }

    public void sortPlaylist ( int playlistIndex ) { 
        
    }

    public void playPlaylist(int playlistIndex, int repeats) {
        /* DO NOT UPDATE THIS METHOD */

        final String NO_SONG_MSG = " has no link to a song! Playing next...";
        if (songLibrary.get(playlistIndex).getLast() == null) {
            StdOut.println("Nothing to play.");
            return;
        }

        SongNode ptr = songLibrary.get(playlistIndex).getLast().getNext(), first = ptr;

        do {
            StdOut.print("\r" + ptr.getSong().toString());
            if (ptr.getSong().getLink() != null) {
                StdAudio.play(ptr.getSong().getLink());
                for (int ii = 0; ii < ptr.getSong().toString().length(); ii++)
                    StdOut.print("\b \b");
            }
            else {
                StdOut.print(NO_SONG_MSG);
                try {
                    Thread.sleep(2000);
                } catch(InterruptedException ex) {
                    ex.printStackTrace();
                }
                for (int ii = 0; ii < NO_SONG_MSG.length(); ii++)
                    StdOut.print("\b \b");
            }

            ptr = ptr.getNext();
            if (ptr == first) repeats--;
        } while (ptr != first || repeats > 0);
    }

    /**
     * ****DO NOT**** UPDATE THIS METHOD
     * Prints playlist by index; can use this method to debug.
     * 
     * @param playlistIndex the playlist to print
     */
    public void printPlaylist(int playlistIndex) {
        StdOut.printf("%nPlaylist at index %d (%d song(s)):%n", playlistIndex, songLibrary.get(playlistIndex).getSize());
        if (songLibrary.get(playlistIndex).getLast() == null) {
            StdOut.println("EMPTY");
            return;
        }
        SongNode ptr;
        for (ptr = songLibrary.get(playlistIndex).getLast().getNext(); ptr != songLibrary.get(playlistIndex).getLast(); ptr = ptr.getNext() ) {
            StdOut.print(ptr.getSong().toString() + " -> ");
        }
        if (ptr == songLibrary.get(playlistIndex).getLast()) {
            StdOut.print(songLibrary.get(playlistIndex).getLast().getSong().toString() + " - POINTS TO FRONT");
        }
        StdOut.println();
    }

    public void printLibrary() {
        if (songLibrary.size() == 0) {
            StdOut.println("\nYour library is empty!");
        } else {
                for (int ii = 0; ii < songLibrary.size(); ii++) {
                printPlaylist(ii);
            }
        }
    }

    /*
     * Used to get and set objects.
     * DO NOT edit.
     */
     public ArrayList<Playlist> getPlaylists() { return songLibrary; }
     public void setPlaylists(ArrayList<Playlist> p) { songLibrary = p; }
}
