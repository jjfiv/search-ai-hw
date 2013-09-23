package edu.umass.cs.jfoley.ai;

public class Utility {

  /*
   * http://stackoverflow.com/questions/9624963/java-simplest-integer-hash
   */
  public static int hash(int x) {
    int hashCode = x;
    hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
    return hashCode ^ (hashCode >>> 7) ^ (hashCode >>> 4);
  }
}
