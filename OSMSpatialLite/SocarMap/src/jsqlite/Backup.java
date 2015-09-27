package jsqlite;

/**
 * Class wrapping an SQLite backup object.
 */

public class Backup {

	/**
	 * Internal native initializer.
	 */

	private static native void internal_init();

	/**
	 * Internal handle for the native SQLite API.
	 */

	protected long handle = 0;

	static {
		internal_init();
	}

	protected native void _finalize() throws jsqlite.Exception;

	private native int _pagecount() throws jsqlite.Exception;

	private native int _remaining() throws jsqlite.Exception;

	private native boolean _step(int n) throws jsqlite.Exception;

	/**
	 * Perform the backup in one step.
	 */

	public void backup() throws jsqlite.Exception {
		synchronized (this) {
			_step(-1);
		}
	}

	/**
	 * Destructor for object.
	 */

	@Override
	protected void finalize() {
		synchronized (this) {
			try {
				_finalize();
			} catch (jsqlite.Exception e) {
			}
		}
	}

	/**
	 * Finish a backup.
	 */

	protected void finish() throws jsqlite.Exception {
		synchronized (this) {
			_finalize();
		}
	}

	/**
	 * Return the total number of pages in the backup source database.
	 */

	public int pagecount() throws jsqlite.Exception {
		synchronized (this) {
			return _pagecount();
		}
	}

	/**
	 * Return number of remaining pages to be backed up.
	 */

	public int remaining() throws jsqlite.Exception {
		synchronized (this) {
			return _remaining();
		}
	}

	/**
	 * Perform a backup step.
	 * 
	 * @param n
	 *            number of pages to backup
	 * @return true when backup completed
	 */

	public boolean step(int n) throws jsqlite.Exception {
		synchronized (this) {
			return _step(n);
		}
	}
}
