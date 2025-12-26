(() => {
    const STORAGE_KEY = 'yostore_cart_v1';

    // utilities
    const $ = sel => document.querySelector(sel);
    const $$ = sel => Array.from(document.querySelectorAll(sel));
    const formatMoney = (num) => {
        if (isNaN(num)) return '0 ₽';
        return new Intl.NumberFormat('ru-RU', { style: 'currency', currency: 'RUB', maximumFractionDigits: 0 }).format(num);
    };

    const readCart = () => {
        try {
            return JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]');
        } catch (e) {
            console.error('Invalid cart JSON', e);
            return [];
        }
    };
    const writeCart = (arr) => {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(arr));
        renderMiniCart();
        renderCartPage();
    };

    // toast
    const showToast = (msg) => {
        let t = $('#toast');
        if (!t) {
            t = document.createElement('div');
            t.id = 'toast';
            t.className = 'toast';
            document.body.appendChild(t);
        }
        t.textContent = msg;
        t.style.display = 'block';
        t.style.opacity = '1';
        clearTimeout(t._hideTimeout);
        t._hideTimeout = setTimeout(() => {
            t.style.opacity = '0';
            setTimeout(()=> t.style.display='none', 250);
        }, 2000);
    };

    // cart operations
    const findIndex = (cart, id) => cart.findIndex(i => String(i.id) === String(id));

    function addToCart(payload) {
        const cart = readCart();
        const idx = findIndex(cart, payload.id);
        if (idx >= 0) {
            cart[idx].qty += payload.qty || 1;
        } else {
            cart.push({
                id: String(payload.id),
                title: payload.title,
                price: Number(payload.price) || 0,
                img: payload.img || null,
                qty: payload.qty || 1,
                brand: payload.brand || ''
            });
        }
        writeCart(cart);
        showToast('Товар добавлен в корзину');
    }

    function removeFromCart(id) {
        let cart = readCart();
        cart = cart.filter(i => String(i.id) !== String(id));
        writeCart(cart);
    }

    function changeQty(id, delta) {
        const cart = readCart();
        const idx = findIndex(cart, id);
        if (idx < 0) return;
        cart[idx].qty = Math.max(1, cart[idx].qty + delta);
        writeCart(cart);
    }

    function clearCart() {
        localStorage.removeItem(STORAGE_KEY);
        renderMiniCart();
        renderCartPage();
    }

    function getTotals() {
        const cart = readCart();
        const totalQty = cart.reduce((s,i) => s + (i.qty||0), 0);
        const totalSum = cart.reduce((s,i) => s + (i.qty||0) * (Number(i.price)||0), 0);
        return { totalQty, totalSum, items: cart };
    }

    // render mini-cart
    function renderMiniCart() {
        const countEl = $('#cartCount');
        const dropdown = $('#miniCartDropdown');
        if (!countEl || !dropdown) return;

        const { totalQty, totalSum, items } = getTotals();
        countEl.textContent = totalQty;

        const container = $('#miniCartItems');
        container.innerHTML = '';

        if (items.length === 0) {
            container.innerHTML = '<div class="empty-note">Корзина пуста</div>';
            $('#miniCartTotal').textContent = formatMoney(0);
            return;
        }

        items.forEach(item => {
            const row = document.createElement('div');
            row.className = 'mini-cart-item';
            row.dataset.id = item.id;

            const img = document.createElement('img');
            if (item.img) {
                img.src = item.img;
                img.alt = item.title;
            } else {
                img.src = '/images/placeholder.png';
                img.alt = item.title;
            }

            const meta = document.createElement('div');
            meta.className = 'meta';
            const title = document.createElement('div');
            title.className = 'title';
            title.textContent = item.title;
            const price = document.createElement('div');
            price.className = 'price';
            price.textContent = formatMoney(item.price * item.qty);

            const qtyControls = document.createElement('div');
            qtyControls.className = 'qty-controls';
            const minus = document.createElement('button');
            minus.textContent = '-';
            minus.className = 'qty-minus';
            minus.dataset.id = item.id;
            const qty = document.createElement('div');
            qty.className = 'qty';
            qty.textContent = item.qty;
            const plus = document.createElement('button');
            plus.textContent = '+';
            plus.className = 'qty-plus';
            plus.dataset.id = item.id;

            qtyControls.append(minus, qty, plus);

            const actions = document.createElement('div');
            actions.className = 'mini-actions';
            const trashBtn = document.createElement('button');
            trashBtn.className = 'icon-trash';
            trashBtn.title = 'Удалить';
            trashBtn.dataset.id = item.id;
            trashBtn.innerHTML = `
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M3 6h18" stroke="#4b5563" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M8 6v12a2 2 0 0 0 2 2h4a2 2 0 0 0 2-2V6" stroke="#4b5563" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M10 11v4" stroke="#4b5563" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M14 11v4" stroke="#4b5563" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M9 6l1-2h4l1 2" stroke="#4b5563" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      `;

            actions.appendChild(trashBtn);

            meta.appendChild(title);
            meta.appendChild(qtyControls);
            meta.appendChild(price);

            row.appendChild(img);
            row.appendChild(meta);
            row.appendChild(actions);

            container.appendChild(row);
        });

        $('#miniCartTotal').textContent = formatMoney(totalSum);
    }

    // render /cart page (if exists)
    function renderCartPage() {
        const cartList = $('#cartList'); // container on /cart page
        const cartSummary = $('#cartSummary');
        if (!cartList) return;

        const { items, totalQty, totalSum } = getTotals();
        cartList.innerHTML = '';

        if (items.length === 0) {
            cartList.innerHTML = '<div class="empty-note">В вашей корзине пока нет товаров.</div>';
            if (cartSummary) cartSummary.innerHTML = '';
            return;
        }

        items.forEach(it => {
            const row = document.createElement('div');
            row.className = 'cart-row';
            row.dataset.id = it.id;

            const img = document.createElement('img');
            img.src = it.img || '/images/placeholder.png';

            const info = document.createElement('div');
            info.className = 'cart-info';
            const title = document.createElement('div');
            title.className = 'title';
            title.textContent = it.title;
            const brand = document.createElement('div');
            brand.className = 'brand';
            brand.textContent = it.brand || '';

            const actions = document.createElement('div');
            actions.className = 'cart-actions';

            const qtyControls = document.createElement('div');
            qtyControls.className = 'qty-controls';
            const minus = document.createElement('button');
            minus.textContent = '-';
            minus.className = 'qty-minus';
            minus.dataset.id = it.id;
            const qty = document.createElement('div');
            qty.className = 'qty';
            qty.textContent = it.qty;
            const plus = document.createElement('button');
            plus.textContent = '+';
            plus.className = 'qty-plus';
            plus.dataset.id = it.id;
            qtyControls.append(minus, qty, plus);

            const price = document.createElement('div');
            price.className = 'price';
            price.textContent = formatMoney(it.price * it.qty);

            const trash = document.createElement('button');
            trash.className = 'icon-trash';
            trash.dataset.id = it.id;
            trash.title = 'Удалить';
            trash.innerHTML = `
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M3 6h18" stroke="#4b5563" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M8 6v12a2 2 0 0 0 2 2h4a2 2 0 0 0 2-2V6" stroke="#4b5563" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M10 11v4" stroke="#4b5563" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M14 11v4" stroke="#4b5563" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M9 6l1-2h4l1 2" stroke="#4b5563" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      `;

            actions.appendChild(qtyControls);
            actions.appendChild(price);
            actions.appendChild(trash);

            info.appendChild(title);
            info.appendChild(brand);

            row.appendChild(img);
            row.appendChild(info);
            row.appendChild(actions);

            cartList.appendChild(row);
        });

        if (cartSummary) {
            cartSummary.innerHTML = `
        <div class="cart-summary">
          <div class="line"><span>Товары:</span><span>${totalQty}</span></div>
          <div class="line"><span>Итого:</span><span>${formatMoney(totalSum)}</span></div>
          <button id="cartCheckoutBtn" class="checkout-btn">Оформить заказ</button>
        </div>
      `;
        }
    }

    // attach global listeners
    function attachListeners() {
        // add-to-cart buttons (delegation)
        document.addEventListener('click', (e) => {
            const add = e.target.closest('.add-to-cart');
            if (add) {
                e.preventDefault();
                const id = add.dataset.id;
                const title = add.dataset.title || add.getAttribute('data-title') || 'Товар';
                const price = add.dataset.price || add.getAttribute('data-price') || 0;
                const img = add.dataset.img || add.getAttribute('data-img') || null;
                const brand = add.dataset.brand || add.getAttribute('data-brand') || '';
                addToCart({ id, title, price, img, brand, qty: 1 });
                return;
            }

            // mini-cart open/close
            const miniBtn = e.target.closest('#miniCartBtn');
            if (miniBtn) {
                const dd = $('#miniCartDropdown');
                if (dd) {
                    dd.classList.toggle('show');
                }
                return;
            }

            // Trash (remove)
            const trash = e.target.closest('.icon-trash');
            if (trash && trash.dataset.id) {
                removeFromCart(trash.dataset.id);
                return;
            }

            // Checkout from mini or page
            if (e.target.closest('#miniCartCheckout') || e.target.closest('#cartCheckoutBtn')) {
                e.preventDefault();
                doCheckout();
                return;
            }

            // mini clear
            if (e.target.closest('#miniCartClear')) {
                clearCart();
                showToast('Корзина очищена');
                return;
            }
        });

        // plus/minus delegation
        document.addEventListener('click', (e) => {
            const plus = e.target.closest('.qty-plus');
            if (plus && plus.dataset.id) {
                changeQty(plus.dataset.id, +1);
            }
            const minus = e.target.closest('.qty-minus');
            if (minus && minus.dataset.id) {
                changeQty(minus.dataset.id, -1);
            }
        });

        // close mini-cart when clicking outside
        document.addEventListener('click', (e) => {
            const dd = $('#miniCartDropdown');
            const btn = $('#miniCartBtn');
            if (!dd) return;
            if (btn && (btn === e.target || btn.contains(e.target))) return;
            if (dd.contains(e.target)) return;
            if (dd.classList.contains('show')) dd.classList.remove('show');
        });

        // handling dynamic elements (update qty displays after DOM changes)
        window.addEventListener('storage', () => {
            // if cart changed in another tab
            renderMiniCart();
            renderCartPage();
        });
    }

    async function doCheckout() {
        const { items, totalSum } = getTotals();
        if (!items.length) {
            showToast('Корзина пуста');
            return;
        }

        try {
            const response = await fetch('/cart/checkout', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ items })
            });

            if (response.ok) {
                // server should create order; clear local cart after success
                clearCart();
                showToast('Заказ создан — спасибо!');
                // optionally redirect to order page if backend returns url
                const json = await response.json().catch(()=>null);
                if (json && json.redirectUrl) location.href = json.redirectUrl;
            } else {
                const txt = await response.text();
                showToast('Ошибка оформления: ' + (txt || response.statusText));
            }
        } catch (err) {
            console.error(err);
            showToast('Не удалось связаться с сервером');
        }
    }

    // init
    function init() {
        attachListeners();
        renderMiniCart();
        renderCartPage();

        // attach handler for elements that may be added later (Thymeleaf page loads)
        // ensure cart count element exists
        if (!$('#cartCount')) {
            // no header on page - nothing to do
        }
    }

    // run
    document.addEventListener('DOMContentLoaded', init);
})();

async function doCheckout() {
    const { items, totalSum } = getTotals();
    if (!items.length) {
        showToast('Корзина пуста');
        return;
    }

    // берем CSRF из meta
    const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
    const headers = { 'Content-Type': 'application/json' };
    if (csrfTokenMeta && csrfHeaderMeta) {
        headers[csrfHeaderMeta.getAttribute('content')] = csrfTokenMeta.getAttribute('content');
    }

    try {
        // оставляем путь /cart/checkout если у тебя контроллер ожидает POST /cart/checkout
        // если у тебя REST контроллер маппит /cart/api/checkout — поменяй URL ниже на /cart/api/checkout
        const response = await fetch('/cart/checkout', {
            method: 'POST',
            headers,
            body: JSON.stringify({ items })
        });

        if (response.ok) {
            // server created order; clear local cart after success
            clearCart();
            showToast('Заказ создан — спасибо!');
            // if backend returns JSON with redirectUrl — переходим
            const json = await response.json().catch(()=>null);
            if (json && json.redirectUrl) location.href = json.redirectUrl;
        } else {
            const txt = await response.text().catch(()=>null);
            showToast('Ошибка оформления: ' + (txt || response.statusText));
            console.error('Checkout failed', response.status, txt);
        }
    } catch (err) {
        console.error(err);
        showToast('Не удалось связаться с сервером');
    }

    document.addEventListener('click', (e) => {
        const fav = e.target.closest('.favorite-btn');
        if (fav && fav.dataset.productId) {
            toggleFavorite(fav.dataset.productId, fav);
        }
    });
}