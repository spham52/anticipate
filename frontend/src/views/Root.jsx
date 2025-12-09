import Navbar from "../components/Navbar";
import "./rootStyle.css"

export default function Root() {
    return (
        <div className="root-container-one">
            <Navbar />
            <div className="root-container-two">
                <div className="root-container-info">
                    <p className="p-info-bold">Anticipate v1</p>
                    <p className="p-info-sub">Your eyes when you're not there</p>
                    <button className="buy-now-button">Buy now</button>
                </div>
                <span className="root-circle">
                </span>
                <img src="/images/pico.png" alt="Anticipate Device" className="pico-image"/>
            </div>
            <div className="root-container-three">
                <p className="p-info-bold-2">Who are we?</p>
                <div className="div-parent-container">
                    <img src="/images/twoguysstock.jpeg" alt="Two men talking" className="two-men-stock-photo"/>
                    <p className="subtext-one">Lorem ipsum dolor sit amet, consectetur adipiscing elit.
                        Cras lacus turpis, mollis id odio eget, placerat dictum metus.
                        Aliquam volutpat, magna et blandit accumsan, neque sapien

                        auctor erat, vel porttitor odio urna non lacus. Suspendisse et
                        mauris a leo dictum auctor. Praesent auctor lobortis iaculis.
                        Duis gravida quis est id ullamcorper. Nulla vulputate, nisl in
                        bibendum euismod, erat odio ultricies mauris, id dapibus </p>
                </div>
            </div>
        </div>
    )
}