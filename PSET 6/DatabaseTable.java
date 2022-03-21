import java.util.Map;

public class DatabaseTable extends Thread {
    // assumes 10 times to increment/decremenet total count, in each thread
    private static final int times = 10;
    int total = 0;
    private static DatabaseTable readTotal = new DatabaseTable();  	// a variable shared by all incrementers
    private static DatabaseTable writeTotal = new DatabaseTable();  	// a variable shared by all incrementers
    public Map<Integer, String[]> data;
    public DatabaseTable(){
        this.data = data;
    }

    private String[] getData(Integer key, String field){
        if (data.containsKey(key)){
            return data.get(key);}
        return null;}


    /**
     * Update table row with given key and fields with values in the rowData parameter.
     * For example, a rowData string might look like "name:Alice, year:24, hometown:Hanover"
     * where name, year and hometown are field names whose values follow the colon.  Multiple
     * <field name>:<field value> pairs may be separated by commas.  You may assume this parameter
     * is properly formatted.
     * If a row already exists in the DatabaseTable with the given key, overwrite its field
     * values with the ones passed in as parameters.  Add new field names/values to the row
     * if necessary.  Create a new row if the table does not have a row with the given key.
     * @param key - unique identifier for a table row
     * @param rowData - string formatted as one or more comma separated <field name>:<field value> pairs
     */
    private void updateData(Integer key, String rowData){
        String[] tokens = rowData.split(",");
        data.put(key,tokens);
    }

    /**
     * Read operations return data stored in the database table and write operations update or insert data into the database table.
     * Multiple threads may try to read and write data in the database table concurrently (you may assume only threads will try
     * to read or write data). Sometimes that concurrency can cause data consistency and deadlocking problems.  Our database
     * will follow these rules to prevent concurrency problems:
     *
     * Multiple read operations can proceed concurrently because they do not change the data in the database table.
     * If any threads are currently reading data, then any write operations must wait until all read operations are
     * complete to ensure all concurrent read operations return the same value (this is called consistency)
     * If a thread is currently writing data to the database, then all other read and write operations must wait until
     * the write operation completes If new read operations occur while a write operation is waiting, the write operation
     * must continue to wait until the new read operations also complete.
     * An easy way to enforce these rules is to count the number of threads currently reading data
     * and also to count the number of threads currently writing data.  But we know from class that simply
     * incrementing an integer can be interrupted.  To combat that problem, provide the following synchronized
     * methods for your DatabaseTable class:
     *
     * takeReadLock – increment a read count instance variable (initially 0) and cause this thread to wait if a write
     * operation is underway (e.g., the write count > 0)
     * releaseReadLock – decrement read count and notify all threads the read lock is released
     * takeWriteLock – increment a write count instance variable (initially 0) and cause this thread to wait
     * if other threads are currently reading data (e.g., read count > 0) or writing data (e.g., write count > 0)
     * releaseWriteLock – decrement write count and notify all threads the write lock is released.
     * Threads attempting a read operation (topic of next question) will call takeReadLock before trying to read data (waiting if necessary), and will call releaseReadLock when they are done.  Write operations function similarly, taking and releasing write locks instead of read locks.
     *
     * Paste your methods into the text box provided.
     */
    public synchronized void takeReadLock() {
        if (total>0){
        total++;}}
    public synchronized void releaseReadLock() {
            if (total>0){

        total--;
    }}
    public synchronized void takeWriteLock() {
        if (total>0){

            total++;
        }}
    public synchronized void releaseWriteLock() {
        if (total>0){

            total--;
        }}
    /**
     * Increments total the specified number of times
     */
    public void run() {
        for (int i = 0; i < times; i++) {
            readTotal.takeReadLock();
            readTotal.releaseReadLock();
            writeTotal.takeWriteLock();
            writeTotal.releaseWriteLock();
        }


}}