document.addEventListener('DOMContentLoaded', () => {
    const hash = window.location.hash.substring(1);
    if (hash) {
        const params = new URLSearchParams(hash);
        const accessToken = params.get('access_token');

        if (accessToken) {
            fetch('/auth/callback', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ accessToken: accessToken })
            })
                .then(response => response.text())
                .then(redirectUrl => {
                    window.location.href = redirectUrl;
                })
                .catch(error => {
                    console.error('Erro ao enviar token para o backend:', error);
                    window.location.href = '/login?error=auth_failed';
                });
        } else {
            window.location.href = '/login?error=token_missing';
        }
    } else {
        window.location.href = '/login?error=no_hash';
    }
});