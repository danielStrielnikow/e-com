import { useState } from "react";
import ProductViewModal from "./ProductViewModal";
import { MdAddShoppingCart, MdOutlineRemoveShoppingCart } from "react-icons/md";
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
}) => {
  const [openProductViewModal, setOpenProductViewModal] = useState(false);
  const btnLoader = false;
  const [selectViewProduct, setSelectViewProduct] = useState("");
  const isAvailable = quantity && Number(quantity) > 0;

  const handleProductView = (product) => {
    setSelectViewProduct(product);
    setOpenProductViewModal(true);
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
        className="w-full overflow-hidden aspect-[3/2]"
      >
        <img
          className="w-full h-full cursor-pointer transition-transform duration-300 transform hover:scale-105"
          src={image}
          alt={productName}
        />
      </div>
      <div className="p-4 ">
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

        <div className="min-h-20 max-h-20 ">
          <p className="text-gray-600 text-sm">
            {truncateText(description, 80)}
          </p>
        </div>

        <div className="flex items-center justtify-between">
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
            disabled={!isAvailable || btnLoader}
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
