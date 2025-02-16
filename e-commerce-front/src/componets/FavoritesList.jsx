import { useState, useEffect } from "react";
import { IoMdHeart } from "react-icons/io";
import { MdAddShoppingCart } from "react-icons/md";
import truncateText from "../utils/truncateText";

const FavoritesList = () => {
  const [favorites, setFavorites] = useState([]);

  useEffect(() => {
    const savedFavorites = JSON.parse(localStorage.getItem("favorites")) || [];
    setFavorites(savedFavorites);
  }, []);

  const handleToggleFavorite = (productId) => {
    const updatedFavorites = favorites.filter((product) => product.id !== productId);
    localStorage.setItem("favorites", JSON.stringify(updatedFavorites));
    setFavorites(updatedFavorites);
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-semibold mb-6 text-center">Shopping lists</h1>
      {favorites.length > 0 ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {favorites.map((product) => (
            <div
              key={product.id}
              className="bg-white border border-gray-200 rounded-lg shadow-lg hover:shadow-xl transition-shadow duration-300"
            >
              <div className="relative">
                <img
                  className="w-full h-full cursor-pointer transition-transform duration-300 transform hover:scale-105"
                  src={product.image}
                  alt={product.productName}
                />
                <button
                  onClick={() => handleToggleFavorite(product.id)}
                  className="absolute top-4 right-4 bg-white p-2 rounded-full shadow-md text-red-500 hover:bg-red-50 transition-all"
                >
                  <IoMdHeart className="text-3xl" />
                </button>
              </div>
              <div className="p-4">
                <h3 className="text-lg font-semibold text-gray-800 mb-2">
                  {truncateText(product.productName, 50)}
                </h3>
                <p className="text-sm text-gray-600 mb-3">{truncateText(product.description, 80)}</p>
                <div className="flex justify-between items-center">
                  <span className="text-xl font-bold text-slate-700">
                    PLN {Number(product.specialPrice || product.price).toFixed(2)}
                  </span>
                  <button className="bg-green-500 text-white py-2 px-4 rounded-lg flex items-center hover:bg-green-600 transition-colors">
                    <MdAddShoppingCart className="mr-2 text-xl" />
                    Add to Cart
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <p className="text-center text-gray-500">Shopping lists is empty - add products</p>
      )}
    </div>
  );
};

export default FavoritesList;
