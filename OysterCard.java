public class OysterCardSystem {

  public static void main(String[] args) {
      OysterCard card = new OysterCard(30.0); // Start with £30 balance

      // Simulate journeys
      System.out.println("-------- Initial Balance: £" + String.format("%.2f", card.getBalance()) + " --------");

      card.touchIn(new Station("Holborn", 1));
      card.touchOut(new Station("Earl's Court", 1, 2));

      // Journey 1 breakdown
      System.out.println("Journey 1: Holborn (Zone 1) -> Earl's Court (Zones 1, 2)");
      System.out.println("  - Fare: Anywhere in Zone 1");
      System.out.println("  - Deducted fare: £2.50");
      System.out.println("  - Remaining balance: £" + card.getBalance() + " --------");

      // Calculate bus fare for the middle journey (Earl's Court -> Chelsea)
      double busFare = calculateBusFare(new Station("Earl's Court", 1, 2), new Station("Chelsea", 1));

      card.deduct(busFare);

      // Journey 2 breakdown (simulated using deduct)
      System.out.println("Journey 2: Earl's Court (Zones 1, 2) -> Chelsea (Zone 1) (Bus journey)");
      System.out.println("  - Fare: Calculated bus fare (£1.80)");
      System.out.println("  - Deducted fare: £" + busFare);
      System.out.println("  - Remaining balance: £" + card.getBalance() + " --------");

      card.touchIn(new Station("Earl's Court", 1, 2));
      card.touchOut(new Station("Hammersmith", 2));

      // Journey 3 breakdown
      System.out.println("Journey 3: Earl's Court (Zones 1, 2) -> Hammersmith (Zone 2)");
      System.out.println("  - Fare: One zone outside Zone 1");
      System.out.println("  - Deducted fare: £2.00");
      System.out.println("  - Remaining balance: £" + String.format("%.2f", card.getBalance()) + " --------");

      System.out.println("Final balance: £" + String.format("%.2f", card.getBalance()));
  }

  public static double calculateBusFare(Station origin, Station destination) {
      return 1.80; // Bus fare
  }
}

public class OysterCard {

  private double balance;
  private Journey currentJourney;

  public OysterCard(double initialBalance) {
    this.balance = initialBalance;
  }

  public void topUp(double amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Top-up amount must be positive");
    }
    balance += amount;
  }

  public void touchIn(Station station) {
    if (balance < getMinimumFare(station)) {
      throw new InsufficientBalanceException();
    }
    balance -= getMaximumFare(); // Deduct temporary maximum fare
    currentJourney = new Journey(station);
  }

  public void touchOut(Station station) {
    if (currentJourney == null) {
      throw new IllegalStateException("Must touch in before touching out");
    }
    double fare = FareCalculator.calculateFare(currentJourney.getOrigin(), station);
    balance += getMaximumFare() - fare; // Refund difference
    currentJourney = null;
  }

  public double getBalance() {
    return balance;
  }

  private double getMinimumFare(Station station) {
    return 1.80; // Minimum fare is bus fare
  }

  private double getMaximumFare() {
    return 3.20; // pre-defined maximum fare
  }

  // Add deduct method
  public void deduct(double amount) {
    if (balance >= amount) {
      balance -= amount;
    } else {
      System.out.println("Insufficient balance for transaction");
    }
  }
}


class Station {
  private String name;
  private int[] zones;

  public Station(String name, int... zones) {
      this.name = name;
      this.zones = zones;
  }

  public String getName() {
      return name;
  }

  public int[] getZones() {
      return zones;
  }
}

class Journey {
  private Station origin;

  public Journey(Station origin) {
      this.origin = origin;
  }

  public Station getOrigin() {
      return origin;
  }
}

class FareCalculator {
  public static double calculateFare(Station origin, Station destination) {
    int originZone = origin.getZones()[0];
    int destinationZone = destination.getZones()[0];
    
    if (originZone == destinationZone) {
      return 2.50; // Within zone 1
    } else if (originZone != destinationZone && (originZone == 1 || destinationZone == 1)) {
      return 2.00; // Any one zone outside zone 1
    } else if ((originZone == 1 && destinationZone == 2) || (originZone == 2 && destinationZone == 1)) {
      return 3.00; // Any two zones including Zone 1 (considering both directions)
    } else if (originZone != destinationZone && (originZone == 1 || destinationZone == 1)) {
      return 2.25; // Any two zones excluding zone 1 (considering both directions)
    } else if (originZone != 1 && destinationZone != 1) {
      return 3.20; // Any three zones
    } else {
      return 1.80; // Any bus journey
    }
  }
}

class InsufficientBalanceException extends RuntimeException {
  public InsufficientBalanceException() {
      super("Insufficient balance for journey");
  }
}

