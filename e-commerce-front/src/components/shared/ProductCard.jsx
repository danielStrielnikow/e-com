import { useState, useEffect } from "react";
import ProductViewModal from "./ProductViewModal";
import { MdAddShoppingCart, MdOutlineRemoveShoppingCart } from "react-icons/md";
import { IoMdHeart, IoMdHeartEmpty } from "react-icons/io";
import truncateText from "../../utils/truncateText";
import { useDispatch } from "react-redux";
import { addToCart } from "../../store/actions";
import toast from "react-hot-toast";
import {CURRENCY} from "../../constants";


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
  const dispatch = useDispatch();

  const handleProductView = (product) => {
    if (!about) {
      setSelectViewProduct(product);
      setOpenProductViewModal(true);
    }
  };

  const addToCartHandler = (cartItem) => {
    dispatch(addToCart(cartItem, 1, toast));
  };

  useEffect(() => {
    const savedFavorites = JSON.parse(localStorage.getItem("favorites")) || [];
    setIsFavorite(savedFavorites.some((product) => product.id === productId));
  }, [productId]);

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

    if (savedFavorites.some((product) => product.id === productId)) {
      updatedFavorites = savedFavorites.filter(
        (product) => product.id !== productId
      );
    } else {
      updatedFavorites = [...savedFavorites, productToAdd];
    }

    localStorage.setItem("favorites", JSON.stringify(updatedFavorites));
    setIsFavorite(updatedFavorites.some((product) => product.id === productId));
  };

  return (
    <div className="border rounded-lg shadow-xl overflow-hidden transition-shadow duration-300 relative group w-full h-[400px] flex flex-col">
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
        className="w-full h-48 overflow-hidden relative"
      >
        <img
          className="w-full h-full object-cover cursor-pointer transition-transform duration-300 transform hover:scale-105"
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

      <div className="p-4 flex-1 flex flex-col justify-between">
        <div>
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

          <p className="text-gray-600 text-sm">
            {truncateText(description, 80)}
          </p>
        </div>

        {!about && (
          <div className="flex items-end justify-between h-12">
            {" "}
            {specialPrice ? (
              <div className="flex flex-col">
                <span className="text-gray-400 line-through">
                  {CURRENCY} {Number(price).toFixed(2)}
                </span>
                <span className="text-xl font-bold text-slate-700">
                  {CURRENCY} {Number(specialPrice).toFixed(2)}
                </span>
              </div>
            ) : (
              <span className="text-xl font-bold text-slate-700">
                {CURRENCY} {Number(price).toFixed(2)}
              </span>
            )}
            <button
              disabled={!isAvailable}
              onClick={() =>
                addToCartHandler({
                  image,
                  productName,
                  description,
                  specialPrice,
                  price,
                  productId,
                  quantity,
                })
              }
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
