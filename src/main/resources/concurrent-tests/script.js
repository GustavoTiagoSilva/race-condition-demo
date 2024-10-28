import http from 'k6/http';
import { sleep } from 'k6';

// Array com os nomes de usuário
const users = ['admin1@example.com', 'admin2@example.com'];

// Configuração do teste
export const options = {
  vus: 2,         // Define o número de VUs (usuários virtuais) para o teste
  iterations: 2,  // Define o número total de iterações que o teste deve realizar
};

// Função para escolher um usuário aleatoriamente
function getRandomUser() {
  if (users.length === 0) {
    console.error('Todos os usuários foram utilizados!');
    return null;
  }

  const randomIndex = Math.floor(Math.random() * users.length);
  const user = users[randomIndex];

  // Remove o usuário do array para evitar repetição
  users.splice(randomIndex, 1);

  return user;
}

export default function () {
  // Seleciona um nome de usuário de forma randômica
  const userName = getRandomUser();
  if (!userName) return; // Se não houver mais usuários, interrompe a execução

  // Faz uma requisição GET para o endpoint desejado, incluindo o nome do usuário como parâmetro
  const url = `http://localhost:8080/api/v1/users/6/raceCondition/${userName}`;
  const response = http.put(url, {}, {});

  // Log para monitorar a resposta (opcional)
  console.log(`Status da resposta para ${userName}: ${response.status}`);

  // Aguarda 1 segundo antes de terminar a execução do VU
  sleep(1);
}
