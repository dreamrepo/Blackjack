// Program: Blackjack.java
// Programmer: Lucas Schneider
// Date: December 28, 2012
// Purpose:
//

// Import packages
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Blackjack {
	// Define Card class
	public static class Card {
		// Define variables
		public final int suit;
		public final int number;
		public final String type;
		public final int points;

		// Define Card constructor
		public Card(int s, int n) {
			this.suit = s;
			this.number = n;
				switch (this.number) {
				case 1:
					this.type = "ace";
					// aces being 1 or 11 will be handled by
					// the getScore method
					this.points = 11;
					break;
				case 11:
					this.type = "jack";
					this.points = 10;
					break;
				case 12:
					this.type = "queen";
					this.points = 10;
					break;
				case 13:
					this.type = "king";
					this.points = 10;
					break;
				default:
					this.type = Integer.toString(number);
					this.points = number;
					break;
			}
		}
	}

	// Define player class
	public static class Player {
		// Define variables
		public String name;
		public int cash = 200;
		public int bet = 0;
		public boolean stay = false;
		public boolean bust = false;
		public boolean bankrupt = false;
		public ArrayList<Card> hand = new ArrayList<Card>();
		// Define method to return score
		public int getScore() {
			int score = 0;
			int aces = 0;
			for (Card card : this.hand) {
				score += card.points;
				if (card.type.equals("ace")) aces++;
			}
			while (aces > 0) {
				if (score <= 21) break;
				else {
					score -= 10;
					aces--;
				}
			}
			return score;
		}
		// Define method to return a list of cards in one's hand
		public String getHand() {
			String handList = "";
			for (Card card : this.hand) {
				if (!this.hand.get(this.hand.size() - 1).equals(card)) handList += card.type + ", ";
				else handList += card.type;
			}
			return handList;
		}
	}

	// Define Blakcjack method to deal a card to a player
	public static void deal(Player player, ArrayList<Card> deck) {
		Random generator = new Random();
		boolean success = false;
		while (!success) {
			int randomNumber = generator.nextInt(13) + 1;
			int randomSuit = generator.nextInt(4) + 1;
			for (Card card : deck) {
				if (card.suit ==  randomSuit && card.number == randomNumber) {
					success = true;
					Card randomCard = new Card(randomSuit,randomNumber);
					player.hand.add(randomCard);
					deck.remove(randomCard);
				}
			}
		}
	}
	
	public static ArrayList<Card> setDeck() {
		ArrayList<Card> deck = new ArrayList<Card>();
		for (int s = 1; s < 5; s++) {
			for (int n = 1; n < 14; n++) deck.add(new Blackjack.Card(s,n));
		}
		return deck;
	}

	public static ArrayList<Player> setPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		String playerNum = null;
		Object[] playerSelector = {"1","2","3","4","5","6","7"};
		do {
			playerNum = (String) JOptionPane.showInputDialog(
				null,
				"How many players? (1-7)",
				"Blackjack",
				JOptionPane.QUESTION_MESSAGE,
				null,
				playerSelector,
				null
			);
			if (playerNum == null) Blackjack.quitConfirm();
		}
		while (playerNum == null || (playerNum != null && (!playerNum.matches("[1-7]"))));
		int intPlayers = Integer.parseInt(playerNum);
		for (int i = 0; i < intPlayers; i++) {
			players.add(new Player());
			players.get(i).name = JOptionPane.showInputDialog(
				null,
				"Player " + (i+1) + ", enter your name:",
				"Blackjack",
				JOptionPane.QUESTION_MESSAGE
			);
		}
		return players;
	}

	public static int quitConfirm() {
		int quitConfirm = JOptionPane.showConfirmDialog(
			null,
			"Are you sure you want to quit?",
			"Blackjack",
			JOptionPane.YES_NO_OPTION
		);
		if (quitConfirm == JOptionPane.YES_OPTION) {
			JOptionPane.showMessageDialog(
				null,
				"Please play again!",
				"Blackjack",
				JOptionPane.INFORMATION_MESSAGE
			);
			System.exit(0);
			return quitConfirm;
		}
		else return quitConfirm;
	}

	public static void playRound(ArrayList<Player> players, ArrayList<Card> deck) {
		// deal two cards to each player to start the game,
		// and ask for their bet
		for (Player curPlayer : players) {
			if (!curPlayer.bankrupt) {
				Blackjack.deal(curPlayer,deck);
				Blackjack.deal(curPlayer,deck);
				int bet = 0;
				boolean validBet = false;
				do {
					String betStr = JOptionPane.showInputDialog(
						null,
						curPlayer.name + ", you have $" + curPlayer.cash + ".\n" +
						"Please enter an positive integer amount of money for your bet:",
						"Blackjack",
						JOptionPane.QUESTION_MESSAGE
					);
					try {
						bet = Integer.parseInt(betStr);
						if (bet > curPlayer.cash) {
							JOptionPane.showMessageDialog(
								null,
								"Your bet was $" + bet + " but you only have $" + curPlayer.cash + ".",
								"Blackjack",
								JOptionPane.ERROR_MESSAGE
							);
						}
						else {
							curPlayer.bet = bet;
							validBet = true;
						}
					}
					catch (NumberFormatException e) {
						if (betStr == null) Blackjack.quitConfirm();
						else{
							JOptionPane.showMessageDialog(
								null,
								"Please enter a valid bet!",
								"Blackjack",
								JOptionPane.ERROR_MESSAGE
							);
						}
					}
				}
				while (!validBet);
			}

		}

		// while the user hasn't quit and this phase of the game hasn't ended
		boolean roundEnd = false;
		while (!roundEnd) {
			// cound the number of finished players
			int endPlayers = 0;
			// loop through each player
			for (int i = 0; i < players.size(); i++) {
				Player curPlayer = players.get(i);
				// if the current player hasn't bust or stayed and isn't bankrupt
				if (!curPlayer.bust && !curPlayer.stay && !curPlayer.bankrupt) {
					// alert whose turn it is
					JOptionPane.showMessageDialog(
						null,
						curPlayer.name + ", it's your turn!",
						"Blackjack",
						JOptionPane.INFORMATION_MESSAGE
					);
					Object[] options = {"Hit","Stay"};
					int decision = -1;
					int quitConfirm = JOptionPane.NO_OPTION;
					do {
						decision = JOptionPane.showOptionDialog(
							null,
							"The cards in your hand are: " + curPlayer.getHand() + ".\n" +
							"Your score is " + curPlayer.getScore(),
							"Blackjack",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[0]
						);
						switch (decision) {
							case 0:
								Blackjack.deal(curPlayer,deck);
								break;
							case 1:
								curPlayer.stay = true;
								break;
							default:
								quitConfirm = Blackjack.quitConfirm();
						}
					}
					while (quitConfirm == JOptionPane.NO_OPTION && decision == -1);
					if (curPlayer.getScore() > 21) {
						curPlayer.bust = true;
						JOptionPane.showMessageDialog(
							null,
							"You busted! Your final score is " + curPlayer.getScore() + ".",
							"Bust!",
							JOptionPane.ERROR_MESSAGE
						);
					}
				}
				else endPlayers++;
			}
			if (endPlayers == players.size()) roundEnd = true;
		}
	}

	public static void setCash(ArrayList<Player> players, Player dealer) {
		for (Player curPlayer : players) {
			if (!curPlayer.bankrupt) {
				if (curPlayer.getScore() <= 21 && curPlayer.hand.size() >= 5) curPlayer.cash += curPlayer.bet;
				else if (curPlayer.getScore() > 21 || (curPlayer.getScore() <= dealer.getScore() && dealer.getScore() <= 21)) curPlayer.cash -= curPlayer.bet;
				else if (curPlayer.getScore() == 21 && curPlayer.hand.size() == 2) curPlayer.cash += 2 * curPlayer.bet;
				else curPlayer.cash += curPlayer.bet;

				if (curPlayer.cash == 0) {
					curPlayer.bankrupt = true;
					JOptionPane.showMessageDialog(
						null,
						curPlayer.name + ", you lost all of your money! Thank you for playing with us.",
						"Blackjack",
						JOptionPane.ERROR_MESSAGE
					);
				}
			}
		}
	}

	public static void reset(ArrayList<Player> players, ArrayList<Card> deck) {
		for (Player player : players) {
			player.hand = new ArrayList<Card>();
			player.bust = false;
			player.stay = false;
			player.bet = 0;
		}
		deck = Blackjack.setDeck();
	}

	public static void displayScores(ArrayList<Player> players) {
		// display final scores
		int[] finalScores = new int[players.size()];
		String scores = "<html>--- Final Scores ---<br /><table>";
		for (int i = 0; i < players.size(); i++) {
			finalScores[i] = players.get(i).getScore();
			scores += "<tr><td>" + players.get(i).name + ":</td><td>$" + players.get(i).cash + "</td></tr>";
		}
		scores += "</table></html>";
		JLabel display = new JLabel(scores);
		JOptionPane.showMessageDialog(
			null,
			display,
			"Blackjack",
			JOptionPane.INFORMATION_MESSAGE
		);
	}

	public static void main(String[] args) {
		// create list of players
		ArrayList<Player> players = Blackjack.setPlayers();
		// generate deck
		ArrayList<Card> deck = Blackjack.setDeck();
		int repeat = JOptionPane.YES_OPTION;
		while (repeat == JOptionPane.YES_OPTION) {
			// generate dealer
			Player dealer = new Player();
			dealer.name = "Dealer";
			Blackjack.deal(dealer,deck);
			Blackjack.deal(dealer,deck);
			while (dealer.getScore() < 17) Blackjack.deal(dealer,deck);

			// play one round
			Blackjack.playRound(players,deck);

			// show scores
			String roundScores = "<html>--- Round Scores ---<table><tr><td>Dealer:</td><td>" + dealer.getScore() + "</td></tr>";
			int bankruptPlayers = 0;
			for (Player curPlayer : players) roundScores += "<tr><td>" + curPlayer.name + ":</td><td>" + curPlayer.getScore() + "</td></tr>";
			roundScores += "</table></html>";
			JLabel roundDisplay = new JLabel(roundScores);
			JOptionPane.showMessageDialog(
				null,
				roundDisplay,
				"Blackjack",
				JOptionPane.INFORMATION_MESSAGE
			);
			Blackjack.setCash(players, dealer);
			for (Player curPlayer : players) if (curPlayer.bankrupt) bankruptPlayers++;
			if (bankruptPlayers < players.size()) {
				repeat = JOptionPane.showConfirmDialog(
					null,
					"Would you like to play another round?",
					"Blackjack",
					JOptionPane.YES_NO_OPTION
				);
			}
			else repeat = JOptionPane.NO_OPTION;
			if (repeat == JOptionPane.NO_OPTION) {
				break;
			}
			else {
				Blackjack.reset(players,deck);
			}
		}
		Blackjack.displayScores(players);
	}
}
