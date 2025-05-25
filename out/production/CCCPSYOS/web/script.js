// ✅ Global cart initialization
var cart = JSON.parse(localStorage.getItem("cart") || "[]");

// ✅ Signup button (already working)
document.getElementById('signupBtn').onclick = () => auth('signup');

// ✅ Login button logic
document.getElementById('loginSubmitBtn').onclick = () => {
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value.trim();

    if (!email || !password) {
        return showMessage('loginMsg', 'Please enter both email and password.');
    }

    const body = `email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`;

    fetch("/login", {
        method: "POST",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body
    })
        .then(async res => {
            const msg = await res.text();
            if (!res.ok) throw new Error(msg);

            document.getElementById('loginSection').style.display = 'none';
            document.getElementById('storeSection').style.display = 'block';
            renderProducts();
        })
        .catch(err => showMessage('loginMsg', err.message));
};

// ✅ Auth (signup/login shared)
function auth(mode) {
    const name = document.getElementById('name')?.value.trim();
    const email = document.getElementById('username')?.value.trim();
    const password = document.getElementById('password')?.value.trim();
    const phone = document.getElementById('phone')?.value.trim();
    const address = document.getElementById('address')?.value.trim();

    if (!email || !password || (mode === 'signup' && (!name || !phone || !address))) {
        return showMessage('authMsg', 'Please fill in all required fields.');
    }

    let body = `email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`;
    if (mode === 'signup') {
        body += `&name=${encodeURIComponent(name)}&phone=${encodeURIComponent(phone)}&address=${encodeURIComponent(address)}`;
    }

    fetch(`/${mode}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body
    })
        .then(async res => {
            const msg = await res.text();
            if (!res.ok) throw new Error(msg);

            if (mode === 'signup') {
                alert("Successfully signed up! Please log in.");
                showLoginForm();
            } else {
                document.getElementById('authSection').style.display = 'none';
                document.getElementById('storeSection').style.display = 'block';
                renderProducts();
            }
        })
        .catch(err => showMessage('authMsg', err.message));
}

// ✅ Display helpers
function showMessage(id, msg) {
    document.getElementById(id).innerText = msg;
}

function clearMessages() {
    showMessage('loginMsg', '');
    showMessage('authMsg', '');
}

// ✅ View switching
function showSignupForm() {
    document.getElementById('loginSection').style.display = 'none';
    document.getElementById('authSection').style.display = 'block';
    clearMessages();
}

function showLoginForm() {
    document.getElementById('authSection').style.display = 'none';
    document.getElementById('loginSection').style.display = 'block';
    clearMessages();
}

// ✅ Render products dynamically
function renderProducts() {
    fetch("/products")
        .then(res => res.json())
        .then(data => {
            const productList = document.getElementById("productList");
            productList.innerHTML = "";

            if (data.length === 0) {
                productList.innerHTML = "<p>No products available</p>";
                return;
            }

            data.forEach(item => {
                const imageSrc = item.imageUrl && item.imageUrl.trim() !== ""
                    ? item.imageUrl
                    : "https://via.placeholder.com/100?text=No+Image";

                const div = document.createElement("div");
                div.className = "card mb-3";
                div.innerHTML = `
    <div class="row g-0">
        <div class="col-md-2 p-2">
            <img src="${imageSrc}" class="img-fluid rounded border" style="width:100px;height:100px;object-fit:cover;" alt="Product Image">
        </div>
        <div class="col-md-7">
            <div class="card-body">
                <h5 class="card-title">${item.itemName}</h5>
                <p class="card-text">Rs. ${item.price}</p>
                <input type="number" class="form-control w-50" id="qty-${item.itemId}" placeholder="Quantity" min="1" value="1">
            </div>
        </div>
        <div class="col-md-3 d-flex align-items-center justify-content-center">
           <button onclick="addToCart(item)">Add to Cart</button>

        </div>
    </div>
`;
                productList.appendChild(div);
            });

            const viewCartBtn = document.createElement("button");
            viewCartBtn.className = "btn btn-primary";
            viewCartBtn.innerText = "🛒 View Cart";
            viewCartBtn.addEventListener("click", viewCart);

            const viewCartDiv = document.createElement("div");
            viewCartDiv.className = "text-center mt-4";
            viewCartDiv.appendChild(viewCartBtn);

            productList.appendChild(viewCartDiv);
        })
        .catch(err => {
            console.error("Failed to load products:", err);
            document.getElementById("productList").innerHTML = "Failed to load products.";
        });
}

// ✅ Shared reusable addToCart() function
// ✅ 1. Define cart at the very top

// ✅ 2. Then define functions like addToCart()
function addToCart(item) {
    const cart = JSON.parse(localStorage.getItem("cart") || "[]");
    const existing = cart.find(p => p.itemId === item.itemId);

    if (existing) {
        existing.quantity += 1;
    } else {
        cart.push({ ...item, quantity: 1 });
    }

    localStorage.setItem("cart", JSON.stringify(cart));
    alert("🛒 Added to cart!");
}


// ✅ Modern event listener for all "Add to Cart" buttons
document.addEventListener("click", function (e) {
    if (e.target.classList.contains("add-to-cart-btn")) {
        const itemId = parseInt(e.target.dataset.id);
        const itemName = e.target.dataset.name;
        const price = parseFloat(e.target.dataset.price);
        addToCart(itemId, itemName, price); // Traceable usage
    }
});

// ✅ Render cart
function renderCart() {
    const cartWrapper = document.getElementById("cartContainer");
    cartWrapper.innerHTML = "";

    let total = 0;
    const itemsHtml = cart.map(item => {
        const itemTotal = item.price * item.quantity;
        total += itemTotal;
        return `<div>${item.itemName} (x${item.quantity}) - Rs. ${itemTotal}</div>`;
    }).join("");

    const cartSection = document.createElement("div");
    cartSection.id = "cartSection";
    cartSection.className = "mt-4";
    cartSection.innerHTML = `
        <h4>🛒 Your Cart</h4>
        ${itemsHtml || "<div>Your cart is empty.</div>"}
        <hr>
        <div><strong>Total: Rs. ${total}</strong></div>
        <button class="btn btn-primary mt-2" onclick="checkout()">Checkout</button>
    `;

    cartWrapper.appendChild(cartSection);
}

// ✅ Checkout flow
function checkout() {
    if (cart.length === 0) {
        alert("Cart is empty");
        return;
    }

    fetch("/checkout", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cart)
    })
        .then(res => res.text())
        .then(msg => {
            alert("✅ " + msg);
            cart.length = 0;
            renderProducts(); // refresh the view
        })
        .catch(() => alert("❌ Checkout failed"));
}

// ✅ Page redirects
function viewCart() {
    window.location.href = "cart.html";
}

function continueShopping() {
    localStorage.setItem("redirectToShop", "true");
    window.location.href = "store.html";
}
