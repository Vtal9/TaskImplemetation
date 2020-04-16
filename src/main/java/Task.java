import java.util.concurrent.Callable;

public class Task<T> {
    private final Callable<? extends T> task;
    private T result;
    private volatile boolean isRunning = false;
    private volatile boolean isFinished = false;
    private volatile boolean exceptionTrowed = false;

    public Task(Callable<? extends T> callable) {
        this.task = callable;
    }

    public T get() {
        if (!isFinished) {
            synchronized (this) {
                while (isRunning) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isRunning = true;
            }

            try {
                if (!isFinished) {
                    this.result = this.task.call();
                    isFinished = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                exceptionTrowed = true;
            } finally {
                isRunning = false;
            }


        }
        if (exceptionTrowed) {
            throw new TaskException();
        }
        return result;
    }
}

class TaskException extends RuntimeException {
}
