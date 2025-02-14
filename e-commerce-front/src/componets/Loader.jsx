import { InfinitySpin } from "react-loader-spinner";

const Loader = ({text}) => {
  return (
    <div className="flex justify-center items-center w-full h-[450px]">
      <div className="flex flex-col items-center gap-1">
        <InfinitySpin
          visible={true}
          width="200"
          color="#4fa94d"
          ariaLabel="infinity-spin-loading"
        />
        <p className="text-slate-800">
            {text ? text : "Please wait...."}
        </p>
      </div>
    </div>
  );
};

export default Loader;
