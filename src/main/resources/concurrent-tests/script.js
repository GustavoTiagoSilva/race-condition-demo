import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 50,
    duration: '30s',
};

function getRandomAmount() {
    return (Math.random() * 1000).toFixed(2);
}

const sourceAccountId = 'd146c1e9-5e29-4fb2-87c6-8f9c6a7a9f17';
const targetAccountId = '9f40a0b1-6f32-4d3a-946d-cd4b46f1de1c';

export default function () {
    let payload = JSON.stringify({
        amount: getRandomAmount(),
        sourceAccountId: sourceAccountId,
        targetAccountId: targetAccountId,
    });

    let params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let res = http.post('http://localhost:8080/api/v1/accounts', payload, params);

    check(res, {
        'status é 200': (r) => r.status === 200,
    });

    sleep(1);
}