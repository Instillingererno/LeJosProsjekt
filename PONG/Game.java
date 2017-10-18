//Objekt som representerer en runde eller spill, må lagres i minnet
class Game {
// --- Attributer ---
    Player player1;
    Player player2;
    int score1 = 0;
    int score2 = 0;
    int timeUsed = 0;

// --- Metoder ---
    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public int[] getScore() { //Returnere tabell?
        return {-1}; //midlertidig
    }

    public incrementScore(int spiller) {
        this.score1 += spiller == 1 ? 1 : 0; //if spiller == 1 legg til 1 ellers legg til 0
        this.score2 += spiller == 2 ? 1 : 0;
    }
}
