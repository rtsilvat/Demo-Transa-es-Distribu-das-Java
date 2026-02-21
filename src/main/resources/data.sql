-- Seed de contas iniciais (executado apenas se tabela estiver vazia)
INSERT INTO accounts (number, holder, balance, version)
SELECT '001', 'Conta A', 50000.00, 0
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE number = '001');

INSERT INTO accounts (number, holder, balance, version)
SELECT '002', 'Conta B', 30000.00, 0
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE number = '002');

INSERT INTO accounts (number, holder, balance, version)
SELECT '003', 'Conta C', 10000.00, 0
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE number = '003');
