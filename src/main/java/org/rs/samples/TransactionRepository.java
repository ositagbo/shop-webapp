package org.rs.samples;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;

@Singleton
public class TransactionRepository {

	private final List<Transaction> transactions;

	public TransactionRepository() {
		transactions = new ArrayList<>();
	}

	public void addTransaction(Transaction e) {
		transactions.add(e);
	}

/*	public Transaction findTransactionByType(String type) {
		for (Transaction next : transactions) {
			if (next != null && type.equals(next.getType())) {
				return next;
			}
		}
		return null;
	}*/

	public Transaction findTransactionById(long id) {
		for (Transaction next : transactions) {
			if (next != null && id == next.getTransactionId()) {
				return next;
			}
		}
		return null;
	}
	
	public List<Long> getTransactionTypes(String type) {
		List<Long> transIds = new ArrayList<>();
		for (Transaction next : transactions) {
			if (next != null && next.getType().equals(type)) {
				transIds.add(next.getTransactionId());
			}
		}
		return transIds;
	}

	public double getTransactionSumById(long id) {
		double transactionSum = 0.0;
		for (Transaction next : transactions) {
			if (next != null && next.getTransactionId() == id) {
				transactionSum = transactionSum + next.getAmount();
			}
			
			if (next != null && next.getParentId() == id) {
				transactionSum = transactionSum + next.getAmount();
			}
		}		
		return transactionSum;
	}
	
	public List<Transaction> getTransactions() {
		return transactions;
	}
}
