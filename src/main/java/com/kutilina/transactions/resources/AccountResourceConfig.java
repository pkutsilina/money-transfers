package com.kutilina.transactions.resources;

import com.kutilina.transactions.model.Account;
import com.kutilina.transactions.model.MoneyTransaction;
import com.kutilina.transactions.service.AccountService;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/banking/accounts")
public class AccountResourceConfig {
    private AccountService accountService = AccountService.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response getAccountByID(@NotNull @PathParam("id") Long id) {
        Account account = accountService.getAccountById(id);
        return Response.status(200).entity(account).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccounts() {
        List<Account> allAccounts = accountService.getAllAccounts();
        return Response.status(200)
                .entity(allAccounts)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/transfer")
    public Response transfer(
            @NotNull @PathParam("id") Long sourceID,
            @NotNull @QueryParam("target_account_id") Long targetID,
            @NotNull @QueryParam("amount") Double amount) {
        accountService.transferAmount(sourceID, targetID, amount);
        return Response.status(204).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/transactions")
    public Response getTransactions(@NotNull @PathParam("id") Long sourceID) {
        List<MoneyTransaction> transactions = accountService.getTransactionsByAccountID(sourceID);
        return Response.status(200)
                .entity(transactions)
                .build();
    }

}
