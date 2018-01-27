package egistec.csa.client.api;

interface IFPAuthServiceCallback {
    /**
     * Called when the service has a new value for you.
     */

    void postMessage(int what, int arg1, int arg2);
    
    
}
