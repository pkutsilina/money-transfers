CREATE SEQUENCE transactions_seq;
CREATE SEQUENCE accounts_seq;

CREATE TABLE public.transactions (
   id BIGINT DEFAULT transactions_seq.nextval PRIMARY KEY NOT NULL,
   transfer_id UUID NOT NULL,
   transaction_date TIMESTAMP NOT NULL,
   account_id BIGINT NOT NULL,
   amount DOUBLE
);

CREATE TABLE public.accounts (
   id BIGINT DEFAULT accounts_seq.nextval PRIMARY KEY NOT NULL,
   balance DOUBLE,
   PRIMARY KEY (id)
);

ALTER TABLE transactions ADD FOREIGN KEY (account_id) REFERENCES accounts(id);
CREATE INDEX transactions_account_id_idx ON public.transactions(account_id);

INSERT INTO public.accounts (balance) VALUES (34.3);
INSERT INTO public.accounts (balance) VALUES (7.0);
