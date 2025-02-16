import { useState, useEffect } from "react";
import ProductViewModal from "./ProductViewModal";
import { MdAddShoppingCart, MdOutlineRemoveShoppingCart } from "react-icons/md";
import { IoMdHeart, IoMdHeartEmpty } from "react-icons/io";
import truncateText from "../../utils/truncateText";

const ProductCard = ({
  productId,
  productName,
  image,
  description,
  quantity,
  price,
  discount,
  specialPrice,
  about = false,
}) => {
  const [openProductViewModal, setOpenProductViewModal] = useState(false);
  const [selectViewProduct, setSelectViewProduct] = useState("");
  const [isFavorite, setIsFavorite] = useState(false);
  const isAvailable = quantity && Number(quantity) > 0;

  const handleProductView = (product) => {
    if (!about) {
      setSelectViewProduct(product);
      setOpenProductViewModal(true);
    }
  };

  useEffect(() => {
    const savedFavorites = JSON.parse(localStorage.getItem("favorites")) || [];
    setIsFavorite(savedFavorites.some((product) => product.id === productId));
  }, [productId]);

  // Twój kod w ProductCard
  const toggleFavorite = () => {
    const savedFavorites = JSON.parse(localStorage.getItem("favorites")) || [];
    let updatedFavorites;

    const productToAdd = {
      id: productId,
      productName,
      image,
      price,
      quantity,
      discount,
      specialPrice,
      description,
    };

    // Sprawdzamy, czy produkt jest już w ulubionych
    if (savedFavorites.some((product) => product.id === productId)) {
      updatedFavorites = savedFavorites.filter(
        (product) => product.id !== productId
      );
    } else {
      updatedFavorites = [...savedFavorites, productToAdd];
    }

    // Zapisujemy zaktualizowaną listę ulubionych w localStorage
    localStorage.setItem("favorites", JSON.stringify(updatedFavorites));
    setIsFavorite(updatedFavorites.some((product) => product.id === productId));
  };

  return (
    <div className="border rounded-lg shadow-xl overflow-hidden transition-shadow duration-300 relative group">
      <div
        onClick={() => {
          handleProductView({
            id: productId,
            productName,
            image,
            description,
            quantity,
            price,
            discount,
            specialPrice,
          });
        }}
        className="w-full overflow-hidden aspect-[3/2] relative"
      >
        <img
          className="w-full h-full cursor-pointer transition-transform duration-300 transform hover:scale-105"
          src={image}
          alt={productName}
        />
        <button
          onClick={(e) => {
            e.stopPropagation();
            toggleFavorite();
          }}
          className={`absolute top-2 right-2 bg-white/80 rounded-lg p-2 shadow-md 
                       transition-all duration-300
                     hover:bg-gray-200
                     ${
                       isFavorite
                         ? "opacity-100"
                         : "opacity-0 group-hover:opacity-100"
                     }`}
        >
          <span className="text-2xl">
            {isFavorite ? (
              <IoMdHeart className="text-red-500" />
            ) : (
              <IoMdHeartEmpty className="text-red-500" />
            )}
          </span>
        </button>
      </div>

      <div className="p-4">
        <h2
          onClick={() => {
            handleProductView({
              id: productId,
              productName,
              image,
              description,
              quantity,
              price,
              discount,
              specialPrice,
            });
          }}
          className="text-lg font-semibold mb-2 cursor-pointer"
        >
          {truncateText(productName, 50)}
        </h2>

        <div className="min-h-20 max-h-20">
          <p className="text-gray-600 text-sm">
            {truncateText(description, 80)}
          </p>
        </div>

        {!about && (
          <div className="flex items-center justify-between">
            {specialPrice ? (
              <div className="flex flex-col">
                <span className="text-gray-400 line-through">
                  PLN {Number(price).toFixed(2)}
                </span>
                <span className="text-xl font-bold text-slate-700">
                  PLN {Number(specialPrice).toFixed(2)}
                </span>
              </div>
            ) : (
              <span className="text-xl font-bold text-slate-700">
                PLN {Number(price).toFixed(2)}
              </span>
            )}

            <button
              disabled={!isAvailable}
              onClick={() => {}}
              className={`border border-green-500 text-green-500 opacity-0 
              group-hover:opacity-100 transition-opacity duration-300${
                isAvailable
                  ? "opacity-100 hover:bg-green-200"
                  : "opacity-50 cursor-not-allowed"
              } py-2 px-2 rounded-lg items-center transition-colors 
                flex justify-center ml-auto`}
            >
              <span className="text-2xl">
                {isAvailable ? (
                  <MdAddShoppingCart className="text-2xl" />
                ) : (
                  <MdOutlineRemoveShoppingCart className="text-2xl" />
                )}
              </span>
            </button>
          </div>
        )}
      </div>

      <ProductViewModal
        open={openProductViewModal}
        setOpen={setOpenProductViewModal}
        product={selectViewProduct}
        isAvailable={isAvailable}
      />
    </div>
  );
};

export default ProductCard;
