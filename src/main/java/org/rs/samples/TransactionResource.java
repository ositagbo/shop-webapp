package org.rs.samples;

import java.util.List;
import java.util.Random;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/transactionservice")
@ApplicationScoped
public class TransactionResource {
	@EJB
	TransactionRepository bean;

	@GET
	@Path("/transaction")
	@Produces({ MediaType.APPLICATION_JSON })
	public JsonArray getTransactions() {
		List<Transaction> transactions = bean.getTransactions();
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder arrayBuilder = factory.createArrayBuilder();
		
		if (transactions.size() > 0)  {
			for (Transaction next : transactions) {
				JsonObjectBuilder model = factory.createObjectBuilder();
				model.add("transaction_id", next.getTransactionId());
				model.add("type", next.getType());
				model.add("amount", next.getAmount());
				if (next.getParentId() > 0) {
					model.add("parent_id", next.getParentId());
				}
				
				arrayBuilder.add(model.build());
			}
			return arrayBuilder.build();
		} else {
			return null;
		}
	}

	@GET
	@Path("/transaction/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public JsonObject getTransaction(@PathParam("id") long id) {
		Transaction savedTransaction = bean.findTransactionById(id);

		if (savedTransaction != null) {
			JsonBuilderFactory factory = Json.createBuilderFactory(null);
			JsonObjectBuilder model = factory.createObjectBuilder();
			model.add("amount", savedTransaction.getAmount());
			model.add("type", savedTransaction.getType());
			if (savedTransaction.getParentId() > 0) {
				model.add("parent_id", savedTransaction.getParentId());
			}
			return model.build();
		} else {
			return null;
		}
	}
	
	@POST
	@Path("/transaction")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addTransaction(JsonObject transactionObj) throws InvalidTransactionException {
		if (transactionObj.getString("type") == null && 
				transactionObj.getJsonNumber("amount") == null) {
			throw new InvalidTransactionException("Invalid transaction data...");
		}
		
		long transactionId = this.generateId();
		String type = transactionObj.getString("type");
		double amount = transactionObj.getJsonNumber("amount").doubleValue();
		long parentId = 0;
		
		if (transactionObj.getJsonNumber("parent_id") != null) {
			parentId = transactionObj.getJsonNumber("parent_id").longValue();
		} 
		
		Transaction transaction = new Transaction(transactionId, type, amount, parentId);
		bean.addTransaction(transaction);
	}

	@GET
	@Path("/types/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonArray getTransactionTypes(@PathParam("type") String type) {
		List<Long> transactionIds = bean.getTransactionTypes(type);
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder arrayBuilder = factory.createArrayBuilder();
		
		if (transactionIds.size() > 0) {
			for (Long next : transactionIds) {
				arrayBuilder.add(next);
			}
		}
		return arrayBuilder.build();
	}
	
	@GET
	@Path("/sum/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject getTransactionSum(@PathParam("id") long id) {
		double transactionSum = bean.getTransactionSumById(id);
		JsonObject jsonObj = Json.createObjectBuilder()
				.add("sum", transactionSum).build();
		return jsonObj;
	}
	
	private long generateId() {
		long nextId = System.currentTimeMillis() * (new Random()).nextInt();
		String tempId = "" + nextId;
		String itemId = tempId.substring((tempId.length() - 5), (tempId.length()));
		return (new Long(itemId)).longValue();
	}
}
