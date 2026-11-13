import { Icon } from "../Icon";
export function Modal({ title, children, onClose, footer }) {
  return (
    <div className="modal-backdrop" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-head">
          <div>
            <span className="eyebrow">TUDLO</span>
            <h2>{title}</h2>
          </div>
          <button onClick={onClose}>
            <Icon name="close" />
          </button>
        </div>
        <div className="modal-body">{children}</div>
        <div className="modal-footer">{footer}</div>
      </div>
    </div>
  );
}
