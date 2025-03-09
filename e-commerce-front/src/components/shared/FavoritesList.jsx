import { useState, useEffect } from "react";
import ProductViewModal from "./ProductViewModal";
import { IoMdHeart, IoMdHeartDislike } from "react-icons/io";
import { MdAddShoppingCart, MdOutlineRemoveShoppingCart } from "react-icons/md";
import truncateText from "../../utils/truncateText";
import toast from "react-hot-toast";
import { useDispatch } from "react-redux";
import { addToCart } from "../../store/actions";
import {CURRENCY} from "../../constants";

const FavoritesList = () => {
  const [favorites, setFavorites] = useState([]);
  const dispatch = useDispatch();

  useEffect(() => {
    const savedFavorites = JSON.parse(localStorage.getItem("favorites")) || [];
    setFavorites(savedFavorites);
  }, []);

  const toggleFavorite = (productId) => {
    const updatedFavorites = favorites.filter(
      (product) => product.id !== productId
    );
    localStorage.setItem("favorites", JSON.stringify(updatedFavorites));
    setFavorites(updatedFavorites);
    toast.success("Removed from favorites");
  };

  const checkAvailability = (quantity) => {
    return Number(quantity) > 0;
  };

  const addToCartHandler = (cartItem) => {
    dispatch(addToCart(cartItem, 1, toast));
  };

  return (
    <div className="max-w-3xl mx-auto p-6">
      <h1 className="text-3xl font-bold text-center mb-8">Shopping List</h1>

      {favorites.length === 0 ? (
        <div className="text-center text-gray-500 mt-6 flex flex-col items-center">
          <MdOutlineRemoveShoppingCart className="text-5xl text-gray-400" />
          <p className="mt-3 text-lg">
            Shopping lists are empty - add products
          </p>
        </div>
      ) : (
        <div className="space-y-6">
          {favorites.map((product) => (
            <div
              key={product.id}
              className="border rounded-xl shadow-md overflow-hidden transition-shadow duration-300 relative flex items-center p-6 min-h-[180px] max-h-[180px]"
            >
              <img
                onClick={() =>
                  setFavorites(
                    favorites.map((p) =>
                      p.id === product.id ? { ...p, open: true } : p
                    )
                  )
                }
                className="w-40 h-40 object-cover rounded-lg cursor-pointer"
                src={product.image}
                alt={product.productName}
              />

              <div className="ml-6 flex-1 flex flex-col justify-between min-h-[150px] pr-12">
                <h2 className="text-xl font-semibold">
                  {truncateText(product.productName, 60)}
                </h2>
                <p className="text-gray-600 text-md line-clamp-3">
                  {truncateText(product.description, 100)}
                </p>

                <div className="mt-4">
                  {product.specialPrice ? (
                    <div className="flex flex-col">
                      <span className="text-xl font-bold text-slate-700">
                        {CURRENCY} {Number(product.specialPrice).toFixed(2)}
                      </span>
                    </div>
                  ) : (
                    <span className="text-xl font-bold text-slate-700">
                      {CURRENCY} {Number(product.price).toFixed(2)}
                    </span>
                  )}
                </div>
              </div>

              <div className="absolute top-3 right-3 z-10 group">
                <button
                  onClick={() => toggleFavorite(product.id)}
                  className="bg-white rounded-lg p-3 shadow-md transition-all duration-300 hover:bg-gray-200"
                >
                  <span className="text-2xl text-red-500 transition-opacity duration-300">
                    {favorites.some((fav) => fav.id === product.id) ? (
                      <IoMdHeart className="block group-hover:hidden" />
                    ) : (
                      <IoMdHeartEmpty className="block group-hover:hidden" />
                    )}
                    <IoMdHeartDislike className="hidden group-hover:block" />
                  </span>
                </button>
              </div>

              <div className="absolute bottom-3 right-3 z-10">
                <button
                  disabled={!checkAvailability(product.quantity)}
                  onClick={() =>
                    addToCartHandler({
                      productId: product.id,
                      productName: product.productName,
                      image: product.image,
                      description: product.description,
                      specialPrice: product.specialPrice,
                      price: product.price,
                      quantity: product.quantity,
                    })
                  }
                  className={`border border-green-500 text-green-500 py-2 px-2 rounded-lg flex items-center justify-center 
                  transition-opacity duration-300 ${
                    checkAvailability(product.quantity)
                      ? "opacity-100 hover:bg-green-200"
                      : "opacity-50 cursor-not-allowed"
                  }`}
                >
                  <span className="text-3xl">
                    {checkAvailability(product.quantity) ? (
                      <MdAddShoppingCart className="text-3xl" />
                    ) : (
                      <MdOutlineRemoveShoppingCart className="text-3xl" />
                    )}
                  </span>
                </button>
              </div>

              {product.open && (
                <ProductViewModal
                  open={product.open}
                  setOpen={() =>
                    setFavorites(
                      favorites.map((p) =>
                        p.id === product.id ? { ...p, open: false } : p
                      )
                    )
                  }
                  product={product}
                  isAvailable={checkAvailability(product.quantity)}
                />
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default FavoritesList;
