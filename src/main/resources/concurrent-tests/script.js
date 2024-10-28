import http from 'k6/http';
import { check, sleep } from 'k6';

// Configuração do teste
export let options = {
    vus: 50,            // número de usuários virtuais simultâneos
    duration: '30s',    // duração do teste
};

// Função para gerar um valor aleatório para o amount
function getRandomAmount() {
    return (Math.random() * 1000).toFixed(2);  // valor entre 0.00 e 1000.00
}

// UUIDs específicos para source e target
const sourceAccountId = 'd146c1e9-5e29-4fb2-87c6-8f9c6a7a9f17';
const targetAccountId = '9f40a0b1-6f32-4d3a-946d-cd4b46f1de1c';

// Função principal executada por cada usuário virtual
export default function () {
    // Cria o corpo da requisição com dados aleatórios
    let payload = JSON.stringify({
        amount: getRandomAmount(),
        sourceAccountId: sourceAccountId,
        targetAccountId: targetAccountId,
    });

    // Configura os headers
    let params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // Realiza a requisição POST
    let res = http.post('http://localhost:8080/api/v1/accounts', payload, params);

    // Verifica se a resposta foi bem-sucedida
    check(res, {
        'status é 200': (r) => r.status === 200,
    });

    // Espera um curto período antes de repetir (simula o comportamento real dos usuários)
    sleep(1);
}