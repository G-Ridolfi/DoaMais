/* =========================================================
   DoaMais - JavaScript do navegador
   1) validacao client-side dos formularios
   2) previa instantanea de aptidao em /aptidao
   3) confirmacao antes de registrar uma doacao
   O servidor (Java) continua sendo a fonte oficial das regras.
   Responsavel: Maria Vitoria.
   ========================================================= */
(function () {
    "use strict";

    var DIA_MS = 24 * 60 * 60 * 1000;

    /** Data de hoje no formato yyyy-MM-dd (horario local). */
    function hojeIso() {
        var d = new Date();
        var mes = String(d.getMonth() + 1).padStart(2, "0");
        var dia = String(d.getDate()).padStart(2, "0");
        return d.getFullYear() + "-" + mes + "-" + dia;
    }

    /** Converte "yyyy-MM-dd" em Date local (ou null). */
    function lerData(valor) {
        if (!valor) return null;
        var p = valor.split("-");
        if (p.length !== 3) return null;
        return new Date(Number(p[0]), Number(p[1]) - 1, Number(p[2]));
    }

    function formatarBr(data) {
        return String(data.getDate()).padStart(2, "0") + "/" +
            String(data.getMonth() + 1).padStart(2, "0") + "/" + data.getFullYear();
    }

    function idadeEmAnos(nascimento, hoje) {
        var idade = hoje.getFullYear() - nascimento.getFullYear();
        var m = hoje.getMonth() - nascimento.getMonth();
        if (m < 0 || (m === 0 && hoje.getDate() < nascimento.getDate())) idade--;
        return idade;
    }

    function numero(valor) {
        return parseFloat(String(valor || "").replace(",", "."));
    }

    /** Mostra (ou limpa) a mensagem de erro logo abaixo do campo. */
    function marcarErro(campo, mensagem) {
        var proximo = campo.nextElementSibling;
        if (proximo && proximo.classList.contains("erro-campo")) proximo.remove();
        campo.classList.toggle("invalido", !!mensagem);
        campo.setCustomValidity(mensagem || "");
        if (mensagem) {
            var aviso = document.createElement("div");
            aviso.className = "erro-campo";
            aviso.textContent = mensagem;
            campo.insertAdjacentElement("afterend", aviso);
        }
    }

    /** Regras de cada campo; devolve a mensagem de erro ou "". */
    function validarCampo(campo) {
        var valor = (campo.value || "").trim();
        if (campo.required && !valor) return "Campo obrigatório.";
        if (!valor) return "";

        if (campo.name === "cpf" && valor.replace(/\D/g, "").length !== 11) {
            return "O CPF deve ter 11 dígitos.";
        }
        if (campo.name === "peso" && !(numero(valor) > 0)) {
            return "O peso deve ser maior que zero.";
        }
        if (campo.name === "metaDoacoes" && !(parseInt(valor, 10) > 0)) {
            return "A meta deve ser maior que zero.";
        }
        if (campo.type === "date") {
            if (valor < "1900-01-01") {
                return "Informe uma data válida (a partir de 1900).";
            }
            if (campo.max && valor > campo.max) {
                return "A data não pode estar no futuro.";
            }
        }
        if (campo.name === "dataNascimento" && window.location.pathname.indexOf("/doadores") === 0) {
            var nasc = lerData(valor);
            var hojeData = lerData(hojeIso());
            if (nasc && hojeData) {
                var id = idadeEmAnos(nasc, hojeData);
                if (id < 18) return "O doador deve ter pelo menos 18 anos para se cadastrar.";
                if (id > 69) return "A idade máxima para cadastro de doador é 69 anos.";
            }
        }
        if (campo.name === "dataUltimaDoacao" && campo.form) {
            var campoNasc = campo.form.elements.namedItem("dataNascimento");
            var nascDoador = campoNasc ? lerData(campoNasc.value) : null;
            var ultimaDoacao = lerData(valor);
            if (nascDoador && ultimaDoacao) {
                if (ultimaDoacao < nascDoador) {
                    return "A última doação não pode ser anterior à data de nascimento.";
                }
                if (idadeEmAnos(nascDoador, ultimaDoacao) < 18) {
                    return "Data da última doação inválida (anterior aos 18 anos de idade).";
                }
            }
        }
        return "";
    }

    /** 1) Validacao client-side em todos os formularios. */
    function prepararValidacao() {
        var hoje = hojeIso();

        // Impede datas futuras e anos malucos (ex: 1111): min = 1900-01-01, max = hoje
        document.querySelectorAll('input[type="date"]').forEach(function (campo) {
            if (!campo.min) campo.min = "1900-01-01";
            if (!campo.max) campo.max = hoje;
        });

        document.querySelectorAll("form").forEach(function (form) {
            var campos = form.querySelectorAll("input, select, textarea");

            campos.forEach(function (campo) {
                campo.addEventListener("blur", function () {
                    marcarErro(campo, validarCampo(campo));
                });
                campo.addEventListener("input", function () {
                    if (campo.classList.contains("invalido")) marcarErro(campo, validarCampo(campo));
                });
            });

            form.addEventListener("submit", function (evento) {
                var primeiroErro = null;
                campos.forEach(function (campo) {
                    var msg = validarCampo(campo);
                    marcarErro(campo, msg);
                    if (msg && !primeiroErro) primeiroErro = campo;
                });
                if (primeiroErro) {
                    evento.preventDefault();
                    evento.stopImmediatePropagation();   // nao pergunta confirm() se ha erro
                    primeiroErro.focus();
                }
            });
        });
    }

    /**
     * 2) Previa de aptidao (mesmas regras do MotorAptidao):
     *    18 a 69 anos, peso >= 50 kg, intervalo 60 dias (homem) / 90 dias (mulher).
     */
    function prepararPreviaAptidao() {
        if (window.location.pathname.indexOf("/aptidao") !== 0) return;
        var form = document.querySelector("form");
        if (!form || !form.elements.namedItem("dataNascimento")) return;

        var previa = document.createElement("div");
        previa.className = "previa-aptidao";
        previa.setAttribute("aria-live", "polite");
        form.appendChild(previa);

        function valorSexo() {
            var campo = form.elements.namedItem("sexo");
            if (!campo) return "";
            if (campo.value !== undefined) return campo.value;          // select ou RadioNodeList
            var marcado = form.querySelector('input[name="sexo"]:checked');
            return marcado ? marcado.value : "";
        }

        function atualizar() {
            var nascimento = lerData(form.elements.namedItem("dataNascimento").value);
            var pesoCampo = form.elements.namedItem("peso");
            var ultimaCampo = form.elements.namedItem("dataUltimaDoacao");
            var peso = pesoCampo ? numero(pesoCampo.value) : NaN;
            var sexo = valorSexo();

            if (!nascimento || isNaN(peso) || !sexo) {
                previa.innerHTML = "";
                return;
            }

            var hoje = lerData(hojeIso());
            var motivos = [];
            var idade = idadeEmAnos(nascimento, hoje);
            if (idade < 18) motivos.push("idade mínima é 18 anos (você tem " + idade + ")");
            if (idade > 69) motivos.push("idade máxima é 69 anos (você tem " + idade + ")");
            if (peso < 50) motivos.push("peso mínimo é 50 kg");

            var ultima = ultimaCampo ? lerData(ultimaCampo.value) : null;
            if (ultima) {
                var intervalo = sexo === "F" ? 90 : 60;
                var liberacao = new Date(ultima.getTime() + intervalo * DIA_MS);
                if (liberacao > hoje) {
                    motivos.push("intervalo mínimo de " + intervalo + " dias entre doações " +
                        "(liberado a partir de " + formatarBr(liberacao) + ")");
                }
            }

            var div = document.createElement("div");
            if (motivos.length === 0) {
                div.className = "alerta alerta-sucesso";
                div.textContent = "Parece que você está apto! Envie para a confirmação oficial.";
            } else {
                div.className = "alerta alerta-aviso";
                div.textContent = "Atenção: " + motivos.join("; ") + ".";
            }
            previa.innerHTML = "";
            previa.appendChild(div);
        }

        form.addEventListener("input", atualizar);
        form.addEventListener("change", atualizar);
        atualizar();
    }

    /** 3) Confirmacao antes de enviar o formulario de doacao. */
    function prepararConfirmacaoDoacao() {
        document.querySelectorAll("form").forEach(function (form) {
            var ehDoacao = (form.getAttribute("action") || window.location.pathname)
                .indexOf("/doacoes/nova") === 0 && form.elements.namedItem("doadorId");
            if (!ehDoacao) return;
            form.addEventListener("submit", function (evento) {
                if (!window.confirm("Confirmar o registro desta doação?")) {
                    evento.preventDefault();
                }
            });
        });
    }

    /** Destaca no menu o item da pagina atual. */
    function destacarMenu() {
        var caminho = window.location.pathname;
        document.querySelectorAll(".menu a").forEach(function (link) {
            var href = link.getAttribute("href");
            if (href === caminho || (href !== "/" && caminho.indexOf(href) === 0)) {
                link.classList.add("ativo");
            }
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        prepararValidacao();          // registra o submit de validacao primeiro
        prepararConfirmacaoDoacao();  // depois o confirm (so roda se nao houver erro)
        prepararPreviaAptidao();
        destacarMenu();
    });
})();
