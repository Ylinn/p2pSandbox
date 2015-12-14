public interface IVectorClock
{
    /**
     * increments vector clock of the
     * @param pUnit
     */
    public void incrementClock(String pUnit);

    public Integer get(Object key);

    @Override
    public String toString();

    /**
     * vectorClock merging operation. Creates a new vectorClock with the maximum for
     * each element in either clock. Used in Buffer and Process to manipulate clocks.
     *
     * @param pOne - First Clock being merged.
     * @param pTwo - Second Clock being merged.
     *
     * @return A new vectorClock with the maximum for each element in either clock.
     */
    public VectorClock max(VectorClock pOne, VectorClock pTwo);

    /**
     *
     * MUST RETURN ENUM
     *
     * vectorClock compare operation. Returns one of four possible values indicating how
     * clock one relates to clock two:
     *
     * vectorClock.GREATER			If One > Two.
     * vectorClock.EQUAL			If One = Two.
     * vectorClock.SMALLER			If One < Two.
     * vectorClock.SIMULTANEOUS	    If One <> Two.
     *
     * @param pOne - First Clock being compared.
     * @param pTwo - Second Clock being compared.
     *
     * @return vectorComparison value indicating how One relates to Two.
     */
    public int compare(VectorClock pOne, VectorClock pTwo);
}