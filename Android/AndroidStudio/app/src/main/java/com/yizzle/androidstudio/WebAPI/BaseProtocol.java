package com.yizzle.androidstudio.WebAPI;

import java.util.concurrent.Callable;

/**
 * BaseProtcol
 *
 * Abstract base protocol for handshake
 */
public abstract class BaseProtocol {

    protected static final String CONFIRM = "Confirm";
    protected static final String SUCCESS = "Success";
    protected static final String INITIAL_ACTION = "kap";


    public abstract Retval execute();

    public Callable<Retval> getCallable() {
        return new Callable<BaseProtocol.Retval>() {
            public BaseProtocol.Retval call() {
                return execute();
            }
        };
    }

    public class Retval {
        private String sessionId;
        private String symkey;

        public Retval(String sessionId, String symkey) {
            this.sessionId = sessionId;
            this.symkey = symkey;
        }

        public String getSessionId() {
            return this.sessionId;
        }
        public String getSymkey() {
            return this.symkey;
        }
    }
}
