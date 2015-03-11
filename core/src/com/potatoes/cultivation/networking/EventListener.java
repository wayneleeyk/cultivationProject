package com.potatoes.cultivation.networking;

class EventListener implements Runnable {
	private final Client client;

	/**
	 * @param client
	 */
	EventListener(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		while(true){
			try {
				Protocol p = this.client.incomingQueue.take();
				System.out.println("Protocol has arrived ");
				for (ProtocolHandler protocolHandler : this.client.handlers) {
					protocolHandler.handle(p);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}